package org.cloudfoundry.jmxnozzle.jmx;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.j256.simplejmx.server.JmxServer;
import org.cloudfoundry.jmxnozzle.Metric;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class JmxNozzleServer {
    private int registryPort;
    private int serverPort;
    private String metricPrefix;
    private JmxServer server = null;
    private Cache<ObjectName, DynamicJmxBean> dynamicJmxBeans;
    private JmxNozzleServer() {}

    public JmxNozzleServer(int registryPort, int serverPort, String metricPrefix, long expiryTime) {
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
    }

    public boolean start() throws JMException, UnknownHostException {
        InetAddress host = InetAddress.getByName(System.getProperty("java.rmi.server.hostname", "localhost"));
        System.out.println("binding to: " + host.toString());
        server = new JmxServer(host, registryPort, serverPort);
        server.start();

        server.setUsePlatformMBeanServer(true);
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

    public void stop() {
        server.stop();
        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        dynamicJmxBeans.invalidateAll();
    }
}
