package integration;

import com.j256.simplejmx.client.JmxClient;
import org.cloudfoundry.jmxnozzle.App;
import org.cloudfoundry.jmxnozzle.Config;
import org.cloudfoundry.jmxnozzle.Metric;
import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.management.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JMXServerTest {

  JmxNozzleServer server;

  public void startTheServer(boolean withPrefix) throws Exception {
    server = new JmxNozzleServer(44444, 44445, withPrefix ? "opentsdb.nozzle." : "");
    server.start();
  }

  @AfterEach
  public void stopTheServer() {
    server.stop();
  }

  private JmxClient getJmxClient() throws JMException {
    String uri = String.format(
      "service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi",
      "127.0.0.1",
      44445,
      "127.0.0.1",
      44444
    );
    JmxClient client = new JmxClient(uri);
    assertThat(client).isNotNull();
    return client;
  }

  @Test
  public void addMetricToServer() throws Exception {
    startTheServer(false);
    Map<String, String> metrics1Tags = new HashMap<String, String>();
    metrics1Tags.put("deployment", "deployment0");
    metrics1Tags.put("job", "job0");
    metrics1Tags.put("index", "index0");
    metrics1Tags.put("ip","0.0.0.0");
    server.addMetric(new Metric("system.cpu1", 1d, 0, metrics1Tags));

    Map<String, String> metric2Tags = new HashMap<String, String>();
    metric2Tags.put("deployment", "deployment1");
    metric2Tags.put("job", "job1");
    metric2Tags.put("index", "index1");
    metric2Tags.put("ip","1.1.1.1");
    server.addMetric(new Metric("system.cpu2", 2d, 0, metric2Tags));

    JmxClient client = getJmxClient();

    Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
    ObjectName name = new ObjectName("org.cloudfoundry:deployment=deployment0,job=job0,index=index0,ip=0.0.0.0");
    assertThat(beanNames).contains(name);

    Object attribute = client.getAttribute(name, "system.cpu1");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(1d);

    beanNames = client.getBeanNames("org.cloudfoundry");
    name = new ObjectName("org.cloudfoundry:deployment=deployment1,job=job1,index=index1,ip=1.1.1.1");
    assertThat(beanNames).contains(name);

    attribute = client.getAttribute(name, "system.cpu2");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(2d);

    server.addMetric(new Metric("another.metric", 2d, 0, metric2Tags));
    beanNames = client.getBeanNames("org.cloudfoundry");
    name = new ObjectName("org.cloudfoundry:deployment=deployment1,job=job1,index=index1,ip=1.1.1.1");
    assertThat(beanNames).contains(name);

    attribute = client.getAttribute(name, "another.metric");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(2d);
  }

  //@Test
  @DisplayName("When the same metric has timestamps that come out of order")
  public void sameMetricDifferentTimestamps() throws Exception {
    startTheServer(false);
    Map<String, String> metrics1Tags = new HashMap<String, String>();
    metrics1Tags.put("deployment", "deployment0");
    metrics1Tags.put("job", "job0");
    metrics1Tags.put("index", "index0");
    metrics1Tags.put("ip","0.0.0.0");
    server.addMetric(new Metric("system.cpu1", 100d, 100, metrics1Tags));
    server.addMetric(new Metric("system.cpu1", 88d, 88, metrics1Tags));

    JmxClient client = getJmxClient();

    Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
    ObjectName name = new ObjectName("org.cloudfoundry:deployment=deployment0,job=job0,index=index0,ip=0.0.0.0");
    assertThat(beanNames).contains(name);

    Object attribute = client.getAttribute(name, "system.cpu1");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(100d);
  }

  @Test
  @DisplayName("When the prefix is enabled it prepends each metric name")
  public void addPrefixToMetrics() throws Exception {
    startTheServer(true);
    Map<String, String> metrics1Tags = new HashMap<String, String>();
    metrics1Tags.put("deployment", "deployment0");
    metrics1Tags.put("job", "job0");
    metrics1Tags.put("index", "index0");
    metrics1Tags.put("ip","0.0.0.0");
    server.addMetric(new Metric("testingPrefix", 100d, 100, metrics1Tags));

    JmxClient client = getJmxClient();

    Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
    ObjectName name = new ObjectName("org.cloudfoundry:deployment=deployment0,job=job0,index=index0,ip=0.0.0.0");
    assertThat(beanNames).contains(name);

    Object attribute = client.getAttribute(name, "opentsdb.nozzle.testingPrefix");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(100d);
  }
}
