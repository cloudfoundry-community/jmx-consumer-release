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

  @Override
  public void receiver(LoggregatorEgress.EgressRequest request,
                       StreamObserver<LoggregatorEnvelope.Envelope> responseObserver) {



    for(int i = 1; ; i++) {
      LoggregatorEnvelope.Gauge gauge = LoggregatorEnvelope.Gauge.newBuilder()
              .putMetrics(
                      String.format("fakeMetricName%d", i),
                      LoggregatorEnvelope.GaugeValue.newBuilder().setValue(new Double(i)).build()
              )
              .build();
      LoggregatorEnvelope.Envelope envelope = LoggregatorEnvelope.Envelope.newBuilder().setGauge(gauge).build();

      responseObserver.onNext(envelope);
    }
  }

  public void start() throws IOException {
    server = NettyServerBuilder.forPort(12345)
            .addService(this)
            .sslContext(
                    GrpcSslContexts.forServer(new File( "src/test/resources/metrics-server.pem"), new File("src/test/resources/metrics-server.key"))
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
}
