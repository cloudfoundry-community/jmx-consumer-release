package integration;

import org.cloudfoundry.jmxnozzle.Metric;
import org.cloudfoundry.jmxnozzle.Nozzle;
import org.cloudfoundry.loggregator.v2.LoggregatorEgress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
        assertThat(metric.getName()).isEqualTo("fakeGaugeMetricName0");
        assertThat(metric.getValue()).isEqualTo(0d);
        assertThat(metric.getTimestamp()).isGreaterThan(0L);
        assertThat(metric.getDeployment()).isEqualTo("deployment-name");
        assertThat(metric.getJob()).isEqualTo("job-name");
        assertThat(metric.getIndex()).isEqualTo("index-guid");
        assertThat(metric.getIP()).isEqualTo("0.0.0.0");
        Map<String, String> tags = metric.getTags();
        assertThat(tags.size()).isGreaterThan(0);
        assertThat(tags.get("custom_tag")).isEqualTo("custom_value");

        metric = nozzle.getNextMetric();

        assertThat(metric).isInstanceOfAny(Metric.class);
        assertThat(metric.getName()).isEqualTo("fakeCounterMetricName1");
        assertThat(metric.getValue()).isEqualTo(1d);
        assertThat(metric.getTimestamp()).isGreaterThan(0L);
        assertThat(metric.getDeployment()).isEqualTo("deployment-name");
        assertThat(metric.getJob()).isEqualTo("job-name");
        assertThat(metric.getIndex()).isEqualTo("index-guid");
        assertThat(metric.getIP()).isEqualTo("1.1.1.1");
        tags = metric.getTags();
        assertThat(tags.size()).isGreaterThan(0);
        assertThat(tags.get("custom_tag")).isEqualTo("custom_value");
    }

    @Test
    public void onlyRequestsGaugeAndCounterValues() {
        nozzle.getNextMetric();
        assertThat(fakeLoggregator.getEgressRequest().getSelectorsList()).contains(
                LoggregatorEgress.Selector.newBuilder().setCounter(LoggregatorEgress.CounterSelector.newBuilder().build()).build(),
                LoggregatorEgress.Selector.newBuilder().setGauge(LoggregatorEgress.GaugeSelector.newBuilder().build()).build()
        );
    }
}
