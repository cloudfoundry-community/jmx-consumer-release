package org.cloudfoundry.jmxnozzle;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import org.cloudfoundry.loggregator.v2.EgressGrpc;
import org.cloudfoundry.loggregator.v2.LoggregatorEgress;
import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Nozzle {
    private final ManagedChannel channel;
    private EgressGrpc.EgressBlockingStub blockingStub;
    private Iterator<LoggregatorEnvelope.Envelope> envelopes;

    public Nozzle(String host, int port) throws SSLException {
        List<String> ciphers = new ArrayList<>();
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        this.channel = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(GrpcSslContexts.forClient()
                        .clientAuth(ClientAuth.REQUIRE)
                        .keyManager(new File("src/test/resources/metrics-server.pem"), new File("src/test/resources/metrics-server.key"))
                        .trustManager(new File("src/test/resources/metrics-ca.pem"))
                        .ciphers(ciphers, SupportedCipherSuiteFilter.INSTANCE)
                        .build())
                .overrideAuthority("metrics")
                .build();
    }

    public Metric getNextMetric() {
        while (envelopes.hasNext()) {
            LoggregatorEnvelope.Envelope envelope = envelopes.next();
            Map<String, LoggregatorEnvelope.GaugeValue> metricsMap = envelope.getGauge().getMetricsMap();
            Map.Entry<String, LoggregatorEnvelope.GaugeValue> first = metricsMap.entrySet().iterator().next();
            return new Metric(first.getKey(), first.getValue().getValue());
        }
        return null;
    }

    public void start() {
        blockingStub = EgressGrpc.newBlockingStub(channel);

        LoggregatorEgress.EgressRequest request = LoggregatorEgress.EgressRequest.newBuilder().
                build();
        envelopes = blockingStub.receiver(request);
    }
}
