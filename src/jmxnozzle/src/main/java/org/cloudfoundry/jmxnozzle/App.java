package org.cloudfoundry.jmxnozzle;

import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
    public static void main(String[] args) throws Exception {
        JmxNozzleServer jmxServer = new JmxNozzleServer(Config.getRegistryPort(), Config.getServerPort());
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        rootLogger.setLevel(Level.FINEST);
        for (Handler h : handlers) {
            h.setLevel(Level.FINEST);
        }

        jmxServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                jmxServer.stop();
            }
        }, "Shutdown-thread"));

        Nozzle nozzle = new Nozzle(
                Config.getRLPHost(),
                Config.getRLPPort(),
                Config.getCertFile(),
                Config.getKeyFile(),
                Config.getCACertFile(),
                Config.getAuthority()
        );
        nozzle.start();

        while (true) {
            jmxServer.addMetric(nozzle.getNextMetric());
        }
    }
}
