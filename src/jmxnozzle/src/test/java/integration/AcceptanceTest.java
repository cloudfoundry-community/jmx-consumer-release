package integration;

import com.j256.simplejmx.client.JmxClient;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import org.junit.Test;

import javax.management.JMException;
import javax.management.ObjectName;
import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptanceTest {
    private FakeEgressImpl fakeLoggregator;
    @Test()
    public void connectToNozzleAndReadFromJmxServer() throws Exception {
        fakeLoggregator = new FakeEgressImpl();
        fakeLoggregator.start();

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "./build/libs/jmx-nozzle-1.0-SNAPSHOT.jar");
        Process process = pb.start();

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

            Object attribute = client.getAttribute(name, "fakeMetricName1");
            assertThat(attribute).isNotNull();
            assertThat((Double) attribute).isEqualTo(1d);

            attribute = client.getAttribute(name, "fakeMetricName2");
            assertThat(attribute).isNotNull();
            assertThat((Double) attribute).isEqualTo(2d);
        } finally {
            process.destroyForcibly().waitFor();
            fakeLoggregator.stop();
        }
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
}
