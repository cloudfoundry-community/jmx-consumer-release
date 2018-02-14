package org.cloudfoundry.jmxnozzle;

import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
    public static void main(String[] args) throws Exception {
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
        }
    }
}
