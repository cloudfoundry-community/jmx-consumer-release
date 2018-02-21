package unit;

import org.cloudfoundry.jmxconsumer.ingress.Metric;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DescribeMetricTest {

  @Test
  public void metricTakesItsValuesFromCreation() throws Exception {

    Metric zeroMetric = new Metric("", 0d, 0L, new HashMap<String, String>());
    assertThat(zeroMetric).isInstanceOfAny(Metric.class);
    assertThat(zeroMetric.getName()).isEqualTo("");
    assertThat(zeroMetric.getValue()).isEqualTo(0d);
    assertThat(zeroMetric.getTimestamp()).isEqualTo(0L);
    assertThat(zeroMetric.getDeployment()).isEqualTo(null);
    assertThat(zeroMetric.getJob()).isEqualTo(null);
    assertThat(zeroMetric.getIndex()).isEqualTo(null);
    assertThat(zeroMetric.getIP()).isEqualTo(null);
    Map<String, String> tags = zeroMetric.getTags();
    assertThat(tags.size()).isEqualTo(0);

    HashMap<String, String> inputTags= new HashMap<>();
    inputTags.put("deployment", "test-deployment");
    inputTags.put("job", "test-job");
    inputTags.put("index", "test-index");
    inputTags.put("ip", "test-ip");
    inputTags.put("extra", "test-extra-attribute");
    inputTags.put("origin", "test-origin");

    Metric otherMetric = new Metric("otherMetric", 100d, 12345L, inputTags);
    assertThat(otherMetric).isInstanceOfAny(Metric.class);
    assertThat(otherMetric.getName()).isEqualTo("test-origin.otherMetric[extra=test-extra-attribute]");
    assertThat(otherMetric.getValue()).isEqualTo(100d);
    assertThat(otherMetric.getTimestamp()).isEqualTo(12345L);
    assertThat(otherMetric.getDeployment()).isEqualTo("test-deployment");
    assertThat(otherMetric.getJob()).isEqualTo("test-job");
    assertThat(otherMetric.getIndex()).isEqualTo("test-index");
    assertThat(otherMetric.getIP()).isEqualTo("test-ip");
    Map<String, String> outputTags = otherMetric.getTags();
    assertThat(outputTags.get("extra")).isEqualTo("test-extra-attribute");
  }

  @Test
  @DisplayName("With additional tags they get appeneded to the metric name")
  public void metricNameWithTags() {
    HashMap<String, String> inputTags= new HashMap<>();
    inputTags.put("deployment", "test-deployment");
    inputTags.put("job", "test-job");
    inputTags.put("index", "test-index");
    inputTags.put("ip", "test-ip");
    inputTags.put("origin", "test-origin");
    inputTags.put("metric_version","2.0");
    inputTags.put("loggregator","v1");

    Metric otherMetric = new Metric("otherMetric", 100d, 12345L, inputTags);
    assertThat(otherMetric.getName()).isEqualTo("test-origin.otherMetric[loggregator=v1,metric_version=2.0]");
  }

  @Test
  @DisplayName("It ignores tags that are loggregator specific")
  public void metricNameWithLoggregatorV1Tags() {
    HashMap<String, String> inputTags= new HashMap<>();
    inputTags.put("deployment", "test-deployment");
    inputTags.put("job", "test-job");
    inputTags.put("index", "test-index");
    inputTags.put("ip", "test-ip");
    inputTags.put("origin", "test-origin");
    inputTags.put("metric_version","2.0");
    inputTags.put("loggregator","v1");
    inputTags.put("__v1_type", "CounterEvent");

    Metric otherMetric = new Metric("otherMetric", 100d, 12345L, inputTags);
    assertThat(otherMetric.getName()).isEqualTo("test-origin.otherMetric[loggregator=v1,metric_version=2.0]");
  }

  @Test
  @DisplayName("It removes the id, name, and role as a tag from the name")
  public void metricNameWithIDTag() {
    HashMap<String, String> inputTags= new HashMap<>();
    inputTags.put("deployment", "test-deployment");
    inputTags.put("job", "test-job");
    inputTags.put("index", "test-index");
    inputTags.put("ip", "test-ip");
    inputTags.put("origin", "test-origin");
    inputTags.put("metric_version","2.0");
    inputTags.put("loggregator","v1");
    inputTags.put("id", "don't care");
    inputTags.put("name", "don't care");
    inputTags.put("role", "don't care");

    Metric otherMetric = new Metric("otherMetric", 100d, 12345L, inputTags);
    assertThat(otherMetric.getName()).isEqualTo("test-origin.otherMetric[loggregator=v1,metric_version=2.0]");
  }
}

