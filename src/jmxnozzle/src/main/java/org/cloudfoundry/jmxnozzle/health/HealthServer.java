package org.cloudfoundry.jmxnozzle.health;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HealthServer {
    HttpServer server;
    Map<String, Integer> metrics;

    public HealthServer() {
        this.metrics = new HashMap<String, Integer>();
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", new HealthEndpoint(this.metrics));
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public void addToMetric(String metric) {
        int oldValue = metrics.getOrDefault(metric, 0);
        metrics.put(metric, oldValue + 1);
    }

    class HealthEndpoint implements HttpHandler {
        Map<String, Integer> metrics;
        public HealthEndpoint(Map<String, Integer> metrics) {
                this.metrics = metrics;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(200, 0);

            httpExchange.getResponseBody().write( (new Gson()).toJson(metrics).getBytes());
            httpExchange.getResponseBody().close();
        }
    }
}
