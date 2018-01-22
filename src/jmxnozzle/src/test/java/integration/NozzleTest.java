package integration;

import org.cloudfoundry.jmxnozzle.Metric;
import org.cloudfoundry.jmxnozzle.Nozzle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NozzleTest {
    private Nozzle nozzle;
    private FakeEgressImpl fakeLoggregator;

    @BeforeEach
    public void setupRetrievalTest() throws Exception {
        fakeLoggregator = new FakeEgressImpl();
        fakeLoggregator.start();

        nozzle = new Nozzle(
                "localhost",
                12345,
                "src/test/resources/metrics-server.pem",
                "src/test/resources/metrics-server.key",
                "src/test/resources/metrics-ca.pem",
                "metrics"

        );
        nozzle.start();
    }

    @AfterEach
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
