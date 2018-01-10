package org.cloudfoundry.jmxnozzle;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxResource;
import com.j256.simplejmx.server.JmxServer;
import javax.management.JMException;

public class App {

  public static void main(String[] args) throws JMException {
    JmxServer server = new JmxServer(Config.getRegistryPort(),Config.getServerPort());
    server.start();

    JmxBean iAmJmxServer = new JmxBean();
    server.register(iAmJmxServer);
  }

  @JmxResource(domainName="org.cloudfoundry",beanName="i.am.a.jmx.server")
  private static class JmxBean {

    @JmxAttributeField(description = "i.am.jmx.field description")
    private final String name = "Hello, World";
  }
}
