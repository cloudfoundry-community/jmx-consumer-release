package integration;

import com.j256.simplejmx.client.JmxClient;
import org.cloudfoundry.jmxnozzle.App;
import org.junit.Test;

import javax.management.JMException;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JMXServerTest {
  private void serverStart() throws IOException, JMException {
    App.main(new String[]{});
  }

  @Test
  public void thatTheServerAcceptsConnections() throws IOException, JMException {
    serverStart();

    String uri = String.format(
      "service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi",
      "0.0.0.0",
      44445,
      "0.0.0.0",
      44444
    );
    JmxClient client = new JmxClient(uri);
    assertThat(client).isNotNull();

    Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
    assertThat(beanNames).contains(new ObjectName("org.cloudfoundry:name=i.am.a.jmx.server"));
  }
}
