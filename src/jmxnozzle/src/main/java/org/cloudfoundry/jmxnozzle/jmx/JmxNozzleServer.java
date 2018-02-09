package org.cloudfoundry.jmxnozzle.jmx;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import org.cloudfoundry.jmxnozzle.Metric;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JmxNozzleServer {
    private int registryPort;
    private int serverPort;
    private String metricPrefix;
    private Cache<ObjectName, DynamicJmxBean> dynamicJmxBeans;
    private JMXConnectorServer jmxConnectorServer;
    private Map<String, String> env=  new HashMap<>();
    Registry registry;
    private JmxNozzleServer() {}

    public JmxNozzleServer(int registryPort, int serverPort, String metricPrefix, long expiryTime, String passwordFile, String accessFile) {
        this.registryPort = registryPort;
        this.serverPort = serverPort;
        this.metricPrefix = metricPrefix;
        RemovalListener<ObjectName, DynamicJmxBean> unregsisterFromMBeanServer = removal -> {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            try {
                platformMBeanServer.unregisterMBean(removal.getKey());
            } catch (InstanceNotFoundException | MBeanRegistrationException e) {
                e.printStackTrace();
            }
        };
        this.dynamicJmxBeans = CacheBuilder.newBuilder()
                .expireAfterAccess(expiryTime, TimeUnit.MILLISECONDS)
                .removalListener(unregsisterFromMBeanServer)
                .ticker(Ticker.systemTicker())
                .build();

        this.env.put("jmx.remote.x.password.file", passwordFile);
        this.env.put("jmx.remote.x.access.file", accessFile);
    }

    public boolean start() throws JMException, IOException {
        InetAddress host = InetAddress.getByName(System.getProperty("java.rmi.server.hostname", "localhost"));
        System.out.println("binding to: " + host.toString());

        registry = LocateRegistry.createRegistry(registryPort);
        jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
                new JMXServiceURL(String.format("service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi", host.getHostAddress(), serverPort, host.getHostAddress(), registryPort)),
                this.env,
                ManagementFactory.getPlatformMBeanServer()
        );
        jmxConnectorServer.start();

        return (true);
    }

    public void addMetric(Metric metric) throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException, MalformedObjectNameException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        DynamicJmxBean dynamicJmxBean = getDynamicJmxBean(metric);
        dynamicJmxBean.setMetric(metric);
    }

    private DynamicJmxBean getDynamicJmxBean(Metric metric) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        DynamicJmxBean dynamicJmxBean = new DynamicJmxBean(
                metric.getDeployment(),
                metric.getJob(),
                metric.getIndex(),
                metric.getIP(),
                this.metricPrefix
        );
        ObjectName objectName = new ObjectName(dynamicJmxBean.getName());

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

        if (dynamicJmxBeans.getIfPresent(objectName) != null) {
            dynamicJmxBean = dynamicJmxBeans.getIfPresent(objectName);
        } else {
            dynamicJmxBeans.put(objectName, dynamicJmxBean);
            platformMBeanServer.registerMBean(dynamicJmxBean, objectName);
        }
        return dynamicJmxBean;
    }

    public void stop() throws IOException {
        jmxConnectorServer.stop();
        UnicastRemoteObject.unexportObject(registry, true);
        dynamicJmxBeans.invalidateAll();
    }
}
