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

    public static String getCertFile() {
        return System.getProperty("config.tls.cert", "src/test/resources/metrics-server.pem");
    }

    public static String getKeyFile() {
        return System.getProperty("config.tls.key", "src/test/resources/metrics-server.key");
    }

    public static String getCACertFile() {
        return System.getProperty("config.tls.ca_cert", "src/test/resources/metrics-ca.pem");
    }

    public static String getAuthority() {
        return System.getProperty("config.tls.authority", "metrics");
    }
}
