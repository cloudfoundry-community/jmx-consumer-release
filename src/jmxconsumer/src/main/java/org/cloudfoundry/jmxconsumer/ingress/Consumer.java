package org.cloudfoundry.jmxconsumer.ingress;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudfoundry.loggregator.v2.EgressGrpc;
import org.cloudfoundry.loggregator.v2.LoggregatorEgress;
import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Consumer {
    private static Logger logger = LogManager.getLogger();
    private final NettyChannelBuilder channelBuilder;
    private EgressGrpc.EgressBlockingStub blockingStub;
    private Iterator<LoggregatorEnvelope.Envelope> envelopes;

    public Consumer(String host, int port, String certFile, String keyFile, String caCertFile, String authority) throws SSLException {
        List<String> ciphers = new ArrayList<>();
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        this.channelBuilder = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(GrpcSslContexts.forClient()
                        .clientAuth(ClientAuth.REQUIRE)
                        .keyManager(new File(certFile), new File(keyFile))
                        .trustManager(new File(caCertFile))
                        .ciphers(ciphers, SupportedCipherSuiteFilter.INSTANCE)
                        .build())
                .keepAliveTime(30, TimeUnit.SECONDS)
                .idleTimeout(30, TimeUnit.SECONDS)
                .overrideAuthority(authority);
    }

    public Metric getNextMetric() {
        while (true) {
            try {
                return getMetric();
            } catch(io.grpc.StatusRuntimeException | NoMetricException e) {
                logger.error("Error trying to get next metric: " + e.toString());
                safeSleep(1000);
                envelopes = null;
            }
        }
    }

    private void safeSleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {}
    }

    private Metric getMetric() throws NoMetricException {
        if (envelopes == null || !envelopes.hasNext()) {
            createRequest();
        }
        LoggregatorEnvelope.Envelope envelope = envelopes.next();
        switch (envelope.getMessageCase()) {
            case GAUGE:
                Map<String, LoggregatorEnvelope.GaugeValue> metricsMap = envelope.getGauge().getMetricsMap();
                Map.Entry<String, LoggregatorEnvelope.GaugeValue> first = metricsMap.entrySet().iterator().next();
                return new Metric(first.getKey(), first.getValue().getValue(), envelope.getTimestamp(), envelope.getTagsMap());
            case COUNTER:
                return new Metric(envelope.getCounter().getName(), (double) envelope.getCounter().getTotal(), envelope.getTimestamp(), envelope.getTagsMap());
        }

        throw new NoMetricException();
    }

    private class NoMetricException extends Exception{}

    private void createRequest() {
        logger.debug("Creating new connection to loggregator's rlp");
        ManagedChannel channel = channelBuilder.build();

        blockingStub = EgressGrpc.newBlockingStub(channel);

        LoggregatorEgress.EgressRequest request = LoggregatorEgress.EgressRequest.newBuilder()
                .addSelectors(getCounterSelector())
                .addSelectors(getGaugeSelector())
                .setUsePreferredTags(true)
                .build();
        envelopes = blockingStub.receiver(request);
    }

    private LoggregatorEgress.Selector getGaugeSelector() {
        return LoggregatorEgress.Selector.newBuilder().setGauge(LoggregatorEgress.GaugeSelector.newBuilder().build()).build();
    }

    private LoggregatorEgress.Selector getCounterSelector() {
        return LoggregatorEgress.Selector.newBuilder().setCounter(LoggregatorEgress.CounterSelector.newBuilder().build()).build();
    }
}
