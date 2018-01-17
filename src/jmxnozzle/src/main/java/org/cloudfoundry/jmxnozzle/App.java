package org.cloudfoundry.jmxnozzle;

import org.cloudfoundry.jmxnozzle.jmx.JmxNozzleServer;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class App {
    private static Config config = new Config();

    public static void main(String[] args) throws Exception {
        JmxNozzleServer jmxServer = new JmxNozzleServer();
        Arrays.stream(LogManager.getLogManager().getLogger("").getHandlers()).forEach(h -> h.setLevel(Level.FINER));

        jmxServer.start(config);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                jmxServer.stop();
            }
        }, "Shutdown-thread"));

        Nozzle nozzle = new Nozzle(config.getRLPHost(), config.getRLPPort());
        nozzle.start();

        while (true) {
            jmxServer.addMetric(nozzle.getNextMetric());
        }
    }
}
