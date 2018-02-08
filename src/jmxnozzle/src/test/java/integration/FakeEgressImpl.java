package integration;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.ClientAuth;
import org.cloudfoundry.loggregator.v2.EgressGrpc;
import org.cloudfoundry.loggregator.v2.LoggregatorEgress;
import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FakeEgressImpl extends EgressGrpc.EgressImplBase {
    Server server = null;
    LoggregatorEgress.EgressRequest egressRequest = null;

    @Override
    public void receiver(LoggregatorEgress.EgressRequest request,
                         StreamObserver<LoggregatorEnvelope.Envelope> responseObserver) {


        egressRequest = request;
        for (int i = 0; i <256; i++) {
            LoggregatorEnvelope.Envelope.Builder envelope = LoggregatorEnvelope.Envelope.newBuilder()
                    .setTimestamp(System.nanoTime())
                    .putTags("deployment", "deployment-name")
                    .putTags("job", "job-name")
                    .putTags("index", "index-guid")
                    .putTags("origin", "fakeOrigin")
                    .putTags("id", "some-id")
                    .putTags("ip", String.format("%1d.%1d.%1d.%1d", i, i, i, i))
                    .putTags("custom_tag", "custom_value");

            if (i % 2 == 0) {
                LoggregatorEnvelope.Gauge gauge = LoggregatorEnvelope.Gauge.newBuilder()
                        .putMetrics(
                                String.format("fakeGaugeMetricName%d", i),
                                LoggregatorEnvelope.GaugeValue.newBuilder().setValue(new Double(i)).build()
                        ).build();
                envelope.setGauge(gauge).build();
            } else {
                LoggregatorEnvelope.Counter counter = LoggregatorEnvelope.Counter.newBuilder()
                        .setName(String.format("fakeCounterMetricName%d", i))
                        .setTotal(i)
                        .build();
                envelope.setCounter(counter);
            }

            responseObserver.onNext(envelope.build());
        }
    }

    public void start() throws IOException {
        server = NettyServerBuilder.forPort(12345)
                .addService(this)
                .sslContext(
                        GrpcSslContexts.forServer(new File("src/test/resources/metrics-server.pem"), new File("src/test/resources/metrics-server.key"))
                                .trustManager(new File("src/test/resources/metrics-ca.pem"))
                                .clientAuth(ClientAuth.REQUIRE)
                                .startTls(true)
                                .ciphers(Arrays.asList("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"))
                                .build()
                )
                .build()
                .start();

    }

    public void stop() {
        server.shutdownNow();
    }

    public LoggregatorEgress.EgressRequest getEgressRequest() {
        return egressRequest;
    }
}
