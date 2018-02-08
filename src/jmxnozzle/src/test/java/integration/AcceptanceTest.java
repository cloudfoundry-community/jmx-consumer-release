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
            JmxClient client = getJmxClient();

            Set<ObjectName> beanNames = client.getBeanNames("org.cloudfoundry");
            ObjectName name = new ObjectName("org.cloudfoundry:deployment=deployment-name,job=job-name,index=index-guid,ip=0.0.0.0");
            assertThat(beanNames).contains(name);
            Thread.sleep(1000);

            Object attribute = client.getAttribute(name, "fakeOrigin.fakeGaugeMetricName0[custom_tag=custom_value]");
            assertThat(attribute).isNotNull();
            assertThat((Double) attribute).isEqualTo(0d);

            attribute = client.getAttribute(new ObjectName(
                    "org.cloudfoundry:deployment=deployment-name,job=job-name,index=index-guid,ip=1.1.1.1"),
                    "fakeOrigin.fakeCounterMetricName1[custom_tag=custom_value]");
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

    private JmxClient getJmxClient() throws JMException, InterruptedException {
        String uri = String.format(
                "service:jmx:rmi://%s:%d/jndi/rmi://%s:%d/jmxrmi",
                "127.0.0.1",
                44445,
                "127.0.0.1",
                44444
        );

        int retry = 0;
        int maxRetries = 3;
        JmxClient client = null;
        while(retry < maxRetries) {
            Thread.sleep(1000);
            try {
                client = new JmxClient(uri);
                break;
            } catch(JMException e) {
                retry++;
                if(retry == maxRetries) {
                    throw e;
                }
            }
        }
        waitForJMXServerToPresentBeans(client);
        return client;
    }

    private void waitForJMXServerToPresentBeans(JmxClient client) throws InterruptedException, JMException {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            if(client.getBeanNames("org.cloudfoundry").size() != 0) return;
        }
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
