package org.cloudfoundry.jmxconsumer.jmx;

import org.cloudfoundry.jmxconsumer.ingress.Metric;

import javax.management.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class JmxConsumerServer {

    private JMXServer server;
    private BeanCollector beans;

    private JmxConsumerServer() {}

    public JmxConsumerServer(int registryPort, int serverPort, String metricPrefix, long expiryTime,
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
