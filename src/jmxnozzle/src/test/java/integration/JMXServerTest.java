package integration;

import com.j256.simplejmx.client.JmxClient;
import org.cloudfoundry.jmxnozzle.App;
import org.junit.Before;
import org.junit.Test;

import javax.management.ObjectName;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JMXServerTest {
  private void serverStart() throws Exception {
    App.main(new String[]{});
  }

  @Before
  public void setupConnectionTest() throws Exception {
    serverStart();
  }

  @Test
  public void thatTheServerAcceptsConnections() throws Exception {

    String uri = String.format(
      "service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi",
      "127.0.0.1",
      44445,
      "127.0.0.1",
      44444
    );
    JmxClient client = new JmxClient(uri);
    assertThat(client).isNotNull();

    Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
    assertThat(beanNames).contains(new ObjectName("org.cloudfoundry:name=i.am.a.jmx.server"));
  }
}
