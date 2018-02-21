package org.cloudfoundry.jmxnozzle.jmx;

import org.cloudfoundry.jmxnozzle.ingress.Metric;

import javax.management.*;
import java.io.IOException;

public class JmxNozzleServer {

    private JMXServer server;
    private BeanCollector beans;

    private JmxNozzleServer() {}

    public JmxNozzleServer(int registryPort, int serverPort, String metricPrefix, long expiryTime,
                           String passwordFile, String accessFile, String certFile, String keyFile) throws Exception {
        this.server = new JMXServer(registryPort, serverPort, passwordFile, accessFile, certFile, keyFile);

        this.beans = new BeanCollector(expiryTime, metricPrefix);
    }

    public void start() throws IOException, JMException {
        this.server.start();
    }

    public void addMetric(Metric metric) throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException, MalformedObjectNameException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        beans.setMetric(metric);
    }

    public void stop() throws IOException {
        server.stop();
        beans.stop();
    }
}
