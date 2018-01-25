package unit;

import org.cloudfoundry.jmxnozzle.Metric;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DescribeMetric {

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

    Metric otherMetric = new Metric("otherMetric", 100d, 12345L, inputTags);
    assertThat(otherMetric).isInstanceOfAny(Metric.class);
    assertThat(otherMetric.getName()).isEqualTo("otherMetric");
    assertThat(otherMetric.getValue()).isEqualTo(100d);
    assertThat(otherMetric.getTimestamp()).isEqualTo(12345L);
    assertThat(otherMetric.getDeployment()).isEqualTo("test-deployment");
    assertThat(otherMetric.getJob()).isEqualTo("test-job");
    assertThat(otherMetric.getIndex()).isEqualTo("test-index");
    assertThat(otherMetric.getIP()).isEqualTo("test-ip");
    Map<String, String> outputTags = otherMetric.getTags();
    assertThat(outputTags.get("extra")).isEqualTo("test-extra-attribute");
  }
}

