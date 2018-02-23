package diagnostics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.OutputLogRedirector;
import integration.FakeEgressImpl;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class
HealthEndpointTest {
    Process process;
    FakeEgressImpl fakeLoggregator;

    @BeforeEach
    public void setupTests() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java",
//                "-Dlog4j.configurationFile=/Users/pivotal/workspace/jmx-nozzle-release/src/jmxconsumer/src/test/resources/logging.yml",
                "-jar", "./build/libs/jmx-consumer-1.0-SNAPSHOT.jar"
        );
        process = pb.start();

        fakeLoggregator = new FakeEgressImpl();
        fakeLoggregator.start();

        OutputLogRedirector outputLogRedirector = new OutputLogRedirector();
        outputLogRedirector.writeLogsToStdout(process);
    }

    @AfterEach
    public void shutdown() throws InterruptedException {
        process.destroy();
        process.waitFor();
        fakeLoggregator.stop();

    }

    @Test()
    @DisplayName("Health point receives metrics")
    public void getHealthInfo() throws Exception {
            JsonObject json = getJSONBody("http://localhost:8080/health");

            assertThat(json.get("metrics_received").getAsInt()).isGreaterThan(0);
            assertThat(json.get("metrics_emitted").getAsInt()).isGreaterThan(0);
    }

    private void waitForServerToRespond(HttpGet request, HttpClient client) throws IOException, InterruptedException {
        HttpResponse response= null;
        int numberOfRetrys= 5;

        do {
            System.out.println("retries: " + numberOfRetrys);
            try {
                Thread.sleep(1000);
                response = client.execute(request);
            } catch(Exception e) {
            }

        } while ((response == null) && ((numberOfRetrys--) > 0));
    }

    public JsonObject getJSONBody(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        waitForServerToRespond(request, client);

        HttpResponse response = client.execute(request);
        assertThat(response).isNotNull();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(reader);

        return element.getAsJsonObject();
    }
}