package integration;

import com.j256.simplejmx.client.JmxClient;
import org.junit.jupiter.api.Test;

import javax.management.JMException;
import javax.management.ObjectName;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptanceTest {
    private FakeEgressImpl fakeLoggregator;
    @Test()
    public void connectToNozzleAndReadFromJmxServer() throws Exception {
        fakeLoggregator = new FakeEgressImpl();
        fakeLoggregator.start();

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "./build/libs/jmx-nozzle-1.0-SNAPSHOT.jar");
        Process process = pb.start();

        writeLogsToStdout(process);

        try {
            JmxClient client = null;
            for (int i = 0; i < 1; i++) {
                Thread.sleep(500);
                try {
                    client = getJmxClient();
                } catch (JMException e) {
                    continue;
                }
                break;
            }

            Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
            ObjectName name = new ObjectName("org.cloudfoundry:deployment=deployment,job=job,index=0,ip=0.0.0.0");
            assertThat(beanNames).contains(name);
            Thread.sleep(1000);

            Object attribute = client.getAttribute(name, "fakeGaugeMetricName0");
            assertThat(attribute).isNotNull();
            assertThat((Double) attribute).isEqualTo(0d);

            attribute = client.getAttribute(name, "fakeCounterMetricName1");
            assertThat(attribute).isNotNull();
            assertThat((Double) attribute).isEqualTo(1d);
        } finally {
            process.destroyForcibly().waitFor();
            fakeLoggregator.stop();
        }
    }

    private void writeLogsToStdout(Process process) {
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.out::println);

        new Thread(outputGobbler).start();
        new Thread(errorGobbler).start();
    }

    private JmxClient getJmxClient() throws JMException {
        String uri = String.format(
                "service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi",
                "127.0.0.1",
                44445,
                "127.0.0.1",
                44444
        );
        JmxClient client = new JmxClient(uri);
        assertThat(client).isNotNull();
        return client;
    }

    class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumeInputLine;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumeInputLine) {
            this.inputStream = inputStream;
            this.consumeInputLine = consumeInputLine;
        }

        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumeInputLine);
        }
    }
}
