package unit;

import org.cloudfoundry.jmxnozzle.Metric;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DescribeMetric {

  @Test
  public void metricTakesItsValuesFromCreation() throws Exception {

    Metric zeroMetric = new Metric("", 0d);
    assertThat(zeroMetric).isInstanceOfAny(Metric.class);
    assertThat(zeroMetric.getName()).isEqualTo("");
    assertThat(zeroMetric.getValue()).isEqualTo(0d);

    Metric otherMetric = new Metric("otherMetric", 100d);
    assertThat(otherMetric).isInstanceOfAny(Metric.class);
    assertThat(otherMetric.getName()).isEqualTo("otherMetric");
    assertThat(otherMetric.getValue()).isEqualTo(100d);
  }
}

