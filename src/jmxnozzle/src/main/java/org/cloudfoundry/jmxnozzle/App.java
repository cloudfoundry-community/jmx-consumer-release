package org.cloudfoundry.jmxnozzle;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxResource;
import com.j256.simplejmx.server.JmxServer;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class App {

  public static void main(String[] args) throws Exception {
    Arrays.stream(LogManager.getLogManager().getLogger("").getHandlers()).forEach(h -> h.setLevel(Level.FINER));
    InetAddress host = InetAddress.getByName(System.getProperty("java.rmi.server.hostname", "localhost"));
    System.out.println("binding to: " + host.toString());
    JmxServer server = new JmxServer(host, Config.getRegistryPort(), Config.getServerPort());
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
