package org.cloudfoundry.jmxnozzle.jmx;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxResource;
import com.j256.simplejmx.server.JmxServer;
import org.cloudfoundry.jmxnozzle.Config;
import org.cloudfoundry.jmxnozzle.Metric;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JmxNozzleServer {
  private JmxServer server= null;
  private DynamicJmxBean dynamicJmxBean;

  public boolean start(Config config) throws JMException, UnknownHostException {
    InetAddress host = InetAddress.getByName(System.getProperty("java.rmi.server.hostname", "localhost"));
    System.out.println("binding to: " + host.toString());
    server = new JmxServer(host, config.getRegistryPort(), config.getServerPort());
    server.start();

    server.setUsePlatformMBeanServer(true);
    dynamicJmxBean = new DynamicJmxBean("deployment", "job", "0", "0.0.0.0");

    MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
    platformMBeanServer.registerMBean(dynamicJmxBean, new ObjectName(dynamicJmxBean.getName()));

    return(true);
  }

  public void addMetric(Metric metric) throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
    dynamicJmxBean.setAttribute(new Attribute(metric.getName(), metric.getValue()));
  }

  public void stop() {
    server.stop();
  }
}
