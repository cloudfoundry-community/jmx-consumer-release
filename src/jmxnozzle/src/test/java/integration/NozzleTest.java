package integration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import org.cloudfoundry.jmxnozzle.Metric;
import org.cloudfoundry.jmxnozzle.Nozzle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class NozzleTest {

  private Nozzle nozzle;
  private FakeEgressImpl fakeLoggregator;

  @Before
  public void setupRetrievalTest() throws Exception {
    fakeLoggregator = new FakeEgressImpl();
    fakeLoggregator.start();

    nozzle = new Nozzle("localhost", 12345);
    nozzle.start();
  }

  @After
  public void cleanupServers() {
    fakeLoggregator.stop();
  }

  @Test
  public void thatTheServerCanRetrieveMetrics() throws Exception {
    Metric metric = nozzle.getNextMetric();

    assertThat(metric).isInstanceOfAny(Metric.class);
    assertThat(metric.getName()).isEqualTo("fakeMetricName1");
    assertThat(metric.getValue()).isEqualTo(1d);

    metric = nozzle.getNextMetric();

    assertThat(metric).isInstanceOfAny(Metric.class);
    assertThat(metric.getName()).isEqualTo("fakeMetricName2");
    assertThat(metric.getValue()).isEqualTo(2d);
  }
}
