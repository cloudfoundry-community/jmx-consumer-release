package unit;

import org.cloudfoundry.jmxnozzle.Config;
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


        assertThat(Config.getRLPHost()).isEqualTo("9.9.9.9");
        assertThat(Config.getRLPPort()).isEqualTo(5555);
        assertThat(Config.getRegistryPort()).isEqualTo(2222);
        assertThat(Config.getServerPort()).isEqualTo(1111);
        assertThat(Config.getMetricPrefix()).isEqualTo("testingPrefix");
        assertThat(Config.getPasswordFile()).isEqualTo("someplace/password.cfg");
        assertThat(Config.getAccessFile()).isEqualTo("someplace/access.cfg");
    }

    @Test
    @DisplayName("Setting the mutual TLS config parameters")
    public void testOverridingMutualTLS() {
        assertThat(Config.getCertFile()).isEqualTo("src/test/resources/metrics-server.pem");
        assertThat(Config.getKeyFile()).isEqualTo("src/test/resources/metrics-server.key");
        assertThat(Config.getCACertFile()).isEqualTo("src/test/resources/metrics-ca.pem");
        assertThat(Config.getAuthority()).isEqualTo("metrics");

        System.setProperty("config.tls.cert", "my-cert.pem");
        System.setProperty("config.tls.key", "my.key");
        System.setProperty("config.tls.ca_cert", "ca.pem");
        System.setProperty("config.tls.authority", "reverselogproxy");

        assertThat(Config.getCertFile()).isEqualTo("my-cert.pem");
        assertThat(Config.getKeyFile()).isEqualTo("my.key");
        assertThat(Config.getCACertFile()).isEqualTo("ca.pem");
        assertThat(Config.getAuthority()).isEqualTo("reverselogproxy");
    }
}
