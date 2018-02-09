package integration;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("compatibility")
public class CompatibilityTest {
    private MBeanServerConnection createClient() throws IOException {
        System.setProperty("socksProxyHost", "localhost");
        System.setProperty("socksProxyPort", "5000");

        JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi://10.0.4.28:44445/jndi/rmi://10.0.4.28:44444/jmxrmi");
        Map<String, String[]> env = ImmutableMap.of(JMXConnector.CREDENTIALS, new String[]{"admin", "insecure-password"});

        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, env);
        return jmxConnector.getMBeanServerConnection();
    }

    @Test
    public void customMetricsFromVMS() throws JMException, IOException {
        final String guidPattern = "[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}";
        final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
        final String [] jobs= new String[] {"uaa", "diego_cell", "doppler", "nats", "router"};

        String filter= "org.cloudfoundry:deployment=cf,job=%s,index=*,ip=*,*";

        MBeanServerConnection mbeanConn = createClient();

        Set<ObjectName> names = mbeanConn.queryNames(new ObjectName(String.format(filter, "*")), null);
        assertThat(names.size()).as("cloudfoundry does not have any beans").isGreaterThan(0);

        for(String job: jobs) {
            Set<ObjectName> jobNode = mbeanConn.queryNames(new ObjectName(String.format(filter, job)), null);
            assertThat(jobNode.size())
                    .as("job %s does not have any beans", job)
                    .isGreaterThan(0);

            ObjectName objectNode = jobNode.iterator().next();
            String GUID = objectNode.getKeyProperty("index");

            assertThat(objectNode).isNotNull();
            assertThat(GUID)
                    .as("job %s index child node is not GUID", job)
                    .matches(guidPattern);

            String ip = objectNode.getKeyProperty("ip");
            assertThat(ip).isNotNull();
            assertThat(ip)
                    .as("job %s ip child node is not an IP address", job)
                    .matches(ipv4Pattern);

            // given a list of attributes
            String [] attributeNames = new String[]{
                    "opentsdb.nozzle.MetronAgent.numCPUS",
                    "opentsdb.nozzle.loggregator.metron.egress[metric_version=2.0]",
                    "opentsdb.nozzle.loggregator.metron.average_envelope[loggregator=v1,metric_version=2.0]"
            };

            for(String attributeName: attributeNames) {
                Object attribute = mbeanConn.getAttribute(objectNode, attributeName);
                assertThat(attribute)
                        .as("attribute '%s' should not be null in job '%s'", attributeName, job)
                        .isNotNull();
                assertThat((Double)attribute)
                        .as("attribute '%s' should not be greater than zero in job '%s'", attributeName, job)
                        .isGreaterThan(0d);
            }
        }
    }


    @Test
    public void boshMetrics() throws JMException, IOException {
        final String guidPattern = "[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}";
        final String [] jobs= new String[] {"backup-prepare", "clock_global", "cloud_controller", "consul_server",
                "diego_brain", "diego_database","ha_proxy", "loggregator_trafficcontroller", "mysql_monitor",
                "mysql_proxy", "nats", "nfs_server", "router", "syslog_adapter", "syslog_scheduler", "uaa"};

        String filter= "org.cloudfoundry:deployment=cf-*,job=%s,index=*,ip=*,*";

        MBeanServerConnection mbeanConn = createClient();

        Set<ObjectName> names = mbeanConn.queryNames(new ObjectName(String.format(filter, "*")), null);
        assertThat(names.size()).as("bosh does not have any beans").isGreaterThan(0);

        for(String job: jobs) {
            Set<ObjectName> jobNode = mbeanConn.queryNames(new ObjectName(String.format(filter, job)), null);
            assertThat(jobNode.size())
                    .as("job %s does not have any beans", job)
                    .isGreaterThan(0);

            ObjectName objectNode = jobNode.iterator().next();
            String GUID = objectNode.getKeyProperty("index");

            assertThat(objectNode).isNotNull();
            assertThat(GUID)
                    .as("job %s index child node is not GUID", job)
                    .matches(guidPattern);

            String ip = objectNode.getKeyProperty("ip");
            assertThat(ip).isNotNull();
            assertThat(ip)
                    .as("job %s ip child node is not an IP address", job)
                    .isEqualTo("");

            // given a list of attributes
            String [] attributeNames = new String[]{
                    "opentsdb.nozzle.bosh-system-metrics-forwarder.system.healthy",
                    "opentsdb.nozzle.bosh-system-metrics-forwarder.system.mem.kb",
                    "opentsdb.nozzle.bosh-system-metrics-forwarder.system.disk.ephemeral.percent"
            };

            for(String attributeName: attributeNames) {
                Object attribute = mbeanConn.getAttribute(objectNode, attributeName);
                assertThat(attribute)
                        .as("attribute '%s' should not be null in job '%s'", attributeName, job)
                        .isNotNull();
                assertThat((Double)attribute)
                        .as("attribute '%s' should not be greater than zero in job '%s'", attributeName, job)
                        .isGreaterThan(0d);
            }
        }
    }
}
