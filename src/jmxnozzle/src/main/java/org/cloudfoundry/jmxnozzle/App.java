package org.cloudfoundry.jmxnozzle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudfoundry.jmxnozzle.health.HealthServer;
import org.cloudfoundry.jmxnozzle.ingress.Nozzle;
import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class App {
    private static Logger logger = LogManager.getLogger(App.class);
    public static void main(String[] args) {
        try {
            HealthServer healthServer = createHealthServer();
            JmxNozzleServer jmxServer = createJMXServer();
            addShutdownHook(healthServer, jmxServer);

            Nozzle nozzle = startNozzle();

            while (true) {
                jmxServer.addMetric(nozzle.getNextMetric());
                healthServer.addToMetric("metrics_received");
                healthServer.addToMetric("metrics_emitted");
            }
        } catch (Exception e ){
            printStackTrace(e);
            System.exit(10);
        }
    }

    private static void addShutdownHook(HealthServer healthServer, JmxNozzleServer jmxServer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.fatal("Running shutdown");
                jmxServer.stop();
                healthServer.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Shutdown-thread"));
    }

    private static Nozzle startNozzle() throws SSLException {
        logger.info("Starting Nozzle");
        return new Nozzle(
                Config.getRLPHost(),
                Config.getRLPPort(),
                Config.getRLPCertFile(),
                Config.getRLPKeyFile(),
                Config.getRLPCACertFile(),
                Config.getRLPAuthority()
        );
    }

    private static HealthServer createHealthServer() throws IOException {
        logger.info("Starting Health Server");
        HealthServer healthServer = new HealthServer();
        healthServer.start(Config.getHealthPort());
        return healthServer;
    }

    private static JmxNozzleServer createJMXServer() throws Exception {
        logger.info("Starting JMX Server");
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
        return jmxServer;
    }

    private static void printStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.fatal("A Failure occurred: " + sw.toString());
    }
}
