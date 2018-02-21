package integration;

import org.cloudfoundry.jmxconsumer.jmx.JmxNozzleServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SSLSupportTest {
    private int registryPort = 44444;
    private int serverPort = 44445;
    JmxNozzleServer server;

    @BeforeEach
    public void startTheServer() throws Exception {
        String username = "root", password = "root";
        // create tempfile for password and access file
        File passwordFile = writeToConfigFile("password", username + " " + password);
        File authFile = writeToConfigFile("auth", username + " readonly");

        server = new JmxNozzleServer(
                registryPort,
                serverPort,
                "opentsdb.nozzle.",
                999999l,
                passwordFile.getAbsolutePath(),
                authFile.getAbsolutePath(),
                "src/test/resources/jmx_ssl_test.cert",
                "src/test/resources/jmx_ssl_test.key"
        );
        server.start();
        assertThat(correctOpenSsl()).startsWith("1");
    }

    @AfterEach
    public void stopServer() throws IOException {
        server.stop();
    }

    @Test
    public void testAppAllowsForTLS12() throws Exception {
        File output = testSSLVersion("tls1_2", serverPort, "ECDHE-RSA-AES256-GCM-SHA384");
        assertThat(certificateReceived(output)).describedAs("Should support TLS 1.2").isTrue();
    }

    @Test
    public void testAppDoesNotAllowForTLS1() throws Exception {
        File output = testSSLVersion("tls1", serverPort, "ECDHE-RSA-AES256-GCM-SHA384");
        assertThat(certificateReceived(output)).describedAs("Should not allow TLS 1").isFalse();
    }

    @Test
    public void testAppDoesNotAllowForSSL3() throws Exception {
        File output = testSSLVersion("ssl3", serverPort, "ECDHE-RSA-AES256-GCM-SHA384");
        assertThat(certificateReceived(output)).describedAs("Should not allow SSL3").isFalse();
    }

    @Test
    public void testAppSupports128AES_GCMCipherSuite() throws Exception {
        File output = testSSLVersion("tls1_2", serverPort, "ECDHE-RSA-AES128-GCM-SHA256");
        assertThat(lineMatches(output, "Cipher is ECDHE-RSA-AES128-GCM-SHA256")).describedAs("Should support 128 AES GCM cipher").isTrue();
    }

    @Test
    public void testAppSupports256AES_GCMCipherSuite() throws Exception {
        File output = testSSLVersion("tls1_2", serverPort, "ECDHE-RSA-AES256-GCM-SHA384");
        assertThat(lineMatches(output, "Cipher is ECDHE-RSA-AES256-GCM-SHA384")).describedAs("Should support 256 AES GCM cipher").isTrue();
    }

    private String correctOpenSsl() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("openssl", "version");
        File output = File.createTempFile("temp-file-name", ".tmp");
        builder.redirectErrorStream(true);
        builder.redirectOutput(ProcessBuilder.Redirect.to(output));

        Process test = builder.start();

        synchronized (test) {
            test.wait(1000);
        }

        test.destroy();

        List<String> lines = Files.readAllLines(Paths.get(output.getAbsolutePath()), StandardCharsets.UTF_8);
        String version = lines.toString().split(" ")[1];

        return version;
    }

    private File testSSLVersion(String sslVersion, int port, String cipher) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("openssl", "s_client","-" + sslVersion, "-cipher",  cipher ,"-connect", "localhost:" + port);
        File output = File.createTempFile("temp-file-name", ".tmp");
        builder.redirectErrorStream(true);
        builder.redirectOutput(ProcessBuilder.Redirect.to(output));
        Process test = builder.start();

        synchronized (test) {
            test.wait(1000);
        }

        test.destroy();
        return output;
    }

    private boolean lineMatches(File output, String s) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(output.getAbsolutePath()), StandardCharsets.UTF_8);
        for(String line:lines){
            System.out.println("LINE: "+line);
            if (line.contains(s)) return true;
        }
        return false;
    }

    private boolean certificateReceived(File output) throws IOException {
        return lineMatches(output, "O = Cloud Foundry");
    }

    private boolean metricsCertificateReceived(File output) throws IOException {
        return lineMatches(output, "depth=0 C = US, ST = Illinois, L = Chicago, O = \"Example, Co.\", CN = metrics");
    }

    private File writeToConfigFile(String filename, String content) throws IOException {
        File configFile = File.createTempFile(filename, ".cfg");
        BufferedWriter writer= new BufferedWriter(new FileWriter(configFile));
        writer.write(content);
        writer.close();
        return configFile;
    }
}

