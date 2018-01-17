package integration;

import com.j256.simplejmx.client.JmxClient;
import org.cloudfoundry.jmxnozzle.App;
import org.cloudfoundry.jmxnozzle.Config;
import org.cloudfoundry.jmxnozzle.Metric;
import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.JMException;
import javax.management.ObjectName;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JMXServerTest {

  JmxNozzleServer server;

  @Before
  public void startTheServer() throws Exception {
    server = new JmxNozzleServer();
    server.start(new Config());
  }

  @After
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
    server.addMetric(new Metric("system.cpu1", 1d));
    server.addMetric(new Metric("system.cpu2", 2d));

    JmxClient client = getJmxClient();

    Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
    ObjectName name = new ObjectName("org.cloudfoundry:deployment=deployment,job=job,index=0,ip=0.0.0.0");
    assertThat(beanNames).contains(name);

    Object attribute = client.getAttribute(name, "system.cpu1");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(1d);

    attribute = client.getAttribute(name, "system.cpu2");
    assertThat(attribute).isNotNull();
    assertThat((Double)attribute).isEqualTo(2d);
  }
}
