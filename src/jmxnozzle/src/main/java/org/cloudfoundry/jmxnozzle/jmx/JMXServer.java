package org.cloudfoundry.jmxnozzle.jmx;

import logging.LoggingInterceptor;

import javax.management.JMException;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class JMXServer {
    private Map<String, String> env = new HashMap<>();
    private JMXConnectorServer jmxConnectorServer;
    private int registryPort;
    private int serverPort;
    Registry registry;

    public JMXServer(int registryPort, int serverPort, String passwordFile, String accessFile) {
        this.registryPort = registryPort;
        this.serverPort = serverPort;
        this.env.put("jmx.remote.x.password.file", passwordFile);
        this.env.put("jmx.remote.x.access.file", accessFile);
    }

    public void start() throws JMException, IOException {
        InetAddress host = InetAddress.getByName(System.getProperty("java.rmi.server.hostname", "localhost"));
        System.out.println("binding to: " + host.toString());

        registry = LocateRegistry.createRegistry(registryPort);

        jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
                new JMXServiceURL(String.format("service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi", host.getHostAddress(), serverPort, host.getHostAddress(), registryPort)),
                this.env,
                ManagementFactory.getPlatformMBeanServer()
        );

        MBeanServerForwarder proxy = LoggingInterceptor.newProxyInstance();
        jmxConnectorServer.setMBeanServerForwarder(proxy);

        jmxConnectorServer.start();
    }

    public void stop() throws IOException {
        jmxConnectorServer.stop();
        UnicastRemoteObject.unexportObject(registry, true);
    }

}
