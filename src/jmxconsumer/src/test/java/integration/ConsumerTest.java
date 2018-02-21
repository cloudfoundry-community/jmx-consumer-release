package integration;

import org.cloudfoundry.jmxconsumer.ingress.Consumer;
import org.cloudfoundry.jmxconsumer.ingress.Metric;
import org.cloudfoundry.loggregator.v2.LoggregatorEgress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class ConsumerTest {
    private Consumer consumer;
    private FakeEgressImpl fakeLoggregator;

    @BeforeEach
    public void setupRetrievalTest() throws Exception {
        fakeLoggregator = new FakeEgressImpl();
        fakeLoggregator.start();

        consumer = new Consumer(
                "localhost",
                12345,
                "src/test/resources/metrics-server.pem",
                "src/test/resources/metrics-server.key",
                "src/test/resources/metrics-ca.pem",
                "metrics"

        );
    }

    @AfterEach
    public void cleanupServers() throws InterruptedException {
        fakeLoggregator.stop();
    }

    @Test
    public void thatTheServerCanRetrieveMetrics() throws Exception {
        Metric metric = consumer.getNextMetric();

        assertThat(metric).isInstanceOfAny(Metric.class);
        assertThat(metric.getName()).isEqualTo("fakeOrigin.fakeGaugeMetricName0[custom_tag=custom_value]");
        assertThat(metric.getValue()).isEqualTo(0d);
        assertThat(metric.getTimestamp()).isGreaterThan(0L);
        assertThat(metric.getDeployment()).isEqualTo("deployment-name");
        assertThat(metric.getJob()).isEqualTo("job-name");
        assertThat(metric.getIndex()).isEqualTo("index-guid");
        assertThat(metric.getIP()).isEqualTo("0.0.0.0");
        Map<String, String> tags = metric.getTags();
        assertThat(tags.size()).isGreaterThan(0);
        assertThat(tags.get("custom_tag")).isEqualTo("custom_value");

        metric = consumer.getNextMetric();

        assertThat(metric).isInstanceOfAny(Metric.class);
        assertThat(metric.getName()).isEqualTo("fakeOrigin.fakeCounterMetricName1[custom_tag=custom_value]");
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
    @DisplayName("When loggregator kills the stream")
    public void streamGetsKilled() throws IOException, InterruptedException {
        for(int i = 0; i < 10; ++i) assertThat(consumer.getNextMetric()).isNotNull();
        fakeLoggregator.stop();


//        assertThatThrownBy(() -> { consumer.getNextMetric(); })
//                .isInstanceOf(io.grpc.StatusRuntimeException.class);

        new Thread(() -> {
            try {
                fakeLoggregator.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

//        Thread.sleep(1000L);
        for(int i = 0; i < 10; ++i) assertThat(consumer.getNextMetric()).isNotNull();
    }

    @Test
    @DisplayName("When the stream ends it reconnects")
    public void reconnectsOnCompleteStream() throws IOException {
        for(int i = 0; i < 10; ++i) assertThat(consumer.getNextMetric()).isNotNull();
        for(int i = 0; i < 10; ++i) assertThat(consumer.getNextMetric()).isNotNull();
    }

    @Test
    public void onlyRequestsGaugeAndCounterValues() {
        consumer.getNextMetric();
        assertThat(fakeLoggregator.getEgressRequest().getSelectorsList()).contains(
                LoggregatorEgress.Selector.newBuilder().setCounter(LoggregatorEgress.CounterSelector.newBuilder().build()).build(),
                LoggregatorEgress.Selector.newBuilder().setGauge(LoggregatorEgress.GaugeSelector.newBuilder().build()).build()
        );
        assertThat(fakeLoggregator.getEgressRequest().getUsePreferredTags()).isTrue();
    }
}
