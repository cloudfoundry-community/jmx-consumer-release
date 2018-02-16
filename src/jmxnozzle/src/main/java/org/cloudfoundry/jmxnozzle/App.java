package org.cloudfoundry.jmxnozzle;

import org.cloudfoundry.jmxnozzle.health.HealthServer;
import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {

        HealthServer healthServer = new HealthServer();
        healthServer.start(Config.getHealthPort());

        JmxNozzleServer jmxServer = new JmxNozzleServer(
                Config.getRegistryPort(),
                Config.getServerPort(),
                Config.getMetricPrefix(),
                60 * 5 * 1000,
                Config.getPasswordFile(),
                Config.getAccessFile(),
                Config.getServerCertFile(),
                Config.getServerKeyFile()
        );
        jmxServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    jmxServer.stop();
                    healthServer.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Shutdown-thread"));

        Nozzle nozzle = new Nozzle(
                Config.getRLPHost(),
                Config.getRLPPort(),
                Config.getRLPCertFile(),
                Config.getRLPKeyFile(),
                Config.getRLPCACertFile(),
                Config.getRLPAuthority()
        );
        nozzle.start();

        while (true) {
            jmxServer.addMetric(nozzle.getNextMetric());
            healthServer.addToMetric("metrics_received");
            healthServer.addToMetric("metrics_emitted");
        }
    }
}
