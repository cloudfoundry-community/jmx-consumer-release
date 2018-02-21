package unit;

import org.cloudfoundry.jmxconsumer.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigTest {
    @Test
    @DisplayName("The default valus for a config ")
    public void testDefaultConfigValues() {
        assertThat(Config.getRLPHost()).isEqualTo("localhost");
        assertThat(Config.getRLPPort()).isEqualTo(12345);
        assertThat(Config.getRegistryPort()).isEqualTo(44444);
        assertThat(Config.getServerPort()).isEqualTo(44445);
        assertThat(Config.getMetricPrefix()).isEqualTo("");
        assertThat(Config.getPasswordFile()).isEqualTo("src/test/resources/password.cfg");
        assertThat(Config.getAccessFile()).isEqualTo("src/test/resources/access.cfg");
        assertThat(Config.getServerKeyFile()).isNull();
        assertThat(Config.getServerCertFile()).isNull();
    }

    @Test
    @DisplayName("Setting system properties for the config overrides the defaults")
    public void testOverridingDefaultValues() {
        System.setProperty("config.rlp.host", "9.9.9.9");
        System.setProperty("config.rlp.port", "5555");
        System.setProperty("config.jmx.server.port", "1111");
        System.setProperty("config.jmx.registry.port", "2222");
        System.setProperty("config.metric.prefix", "testingPrefix");
        System.setProperty("config.auth.password.file", "someplace/password.cfg");
        System.setProperty("config.auth.access.file", "someplace/access.cfg");
        System.setProperty("config.jmx.tls.key", "server.key");
        System.setProperty("config.jmx.tls.cert", "server.crt");


        assertThat(Config.getRLPHost()).isEqualTo("9.9.9.9");
        assertThat(Config.getRLPPort()).isEqualTo(5555);
        assertThat(Config.getRegistryPort()).isEqualTo(2222);
        assertThat(Config.getServerPort()).isEqualTo(1111);
        assertThat(Config.getMetricPrefix()).isEqualTo("testingPrefix");
        assertThat(Config.getPasswordFile()).isEqualTo("someplace/password.cfg");
        assertThat(Config.getAccessFile()).isEqualTo("someplace/access.cfg");
        assertThat(Config.getServerKeyFile()).isEqualTo("server.key");
        assertThat(Config.getServerCertFile()).isEqualTo("server.crt");
    }

    @Test
    @DisplayName("Setting the mutual TLS config parameters")
    public void testOverridingMutualTLS() {
        assertThat(Config.getRLPCertFile()).isEqualTo("src/test/resources/metrics-server.pem");
        assertThat(Config.getRLPKeyFile()).isEqualTo("src/test/resources/metrics-server.key");
        assertThat(Config.getRLPCACertFile()).isEqualTo("src/test/resources/metrics-ca.pem");
        assertThat(Config.getRLPAuthority()).isEqualTo("metrics");

        System.setProperty("config.consumer.tls.cert", "my-cert.pem");
        System.setProperty("config.consumer.tls.key", "my.key");
        System.setProperty("config.consumer.tls.ca_cert", "ca.pem");
        System.setProperty("config.consumer.tls.authority", "reverselogproxy");

        assertThat(Config.getRLPCertFile()).isEqualTo("my-cert.pem");
        assertThat(Config.getRLPKeyFile()).isEqualTo("my.key");
        assertThat(Config.getRLPCACertFile()).isEqualTo("ca.pem");
        assertThat(Config.getRLPAuthority()).isEqualTo("reverselogproxy");
    }
}
