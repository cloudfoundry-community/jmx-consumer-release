package org.cloudfoundry.jmxnozzle;

public class Config {
    private Config() {
    }

    public static int getServerPort() {
        return Integer.parseInt(System.getProperty("config.jmx.server.port", "44445"));
    }

    public static int getRegistryPort() {
        return Integer.parseInt(System.getProperty("config.jmx.registry.port", "44444"));
    }

    public static String getRLPHost() {
        return System.getProperty("config.rlp.host", "localhost");
    }

    public static int getRLPPort() {
        return Integer.parseInt(System.getProperty("config.rlp.port", "12345"));
    }

    public static String getRLPCertFile() {
        return System.getProperty("config.nozzle.tls.cert", "src/test/resources/metrics-server.pem");
    }

    public static String getRLPKeyFile() {
        return System.getProperty("config.nozzle.tls.key", "src/test/resources/metrics-server.key");
    }

    public static String getRLPCACertFile() {
        return System.getProperty("config.nozzle.tls.ca_cert", "src/test/resources/metrics-ca.pem");
    }

    public static String getRLPAuthority() {
        return System.getProperty("config.nozzle.tls.authority", "metrics");
    }

    public static String getMetricPrefix() {
        return System.getProperty("config.metric.prefix", "");
    }

    public static String getPasswordFile() { return System.getProperty("config.auth.password.file",
            "src/test/resources/password.cfg");
    }

    public static String getAccessFile() {  return System.getProperty("config.auth.access.file",
            "src/test/resources/access.cfg");
    }

    public static String getServerKeyFile() {
        return System.getProperty("config.jmx.tls.key",null);
    }

    public static String getServerCertFile() {
        return System.getProperty("config.jmx.tls.cert", null);
    }
}
