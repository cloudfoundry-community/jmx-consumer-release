package org.cloudfoundry.jmxconsumer.health;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudfoundry.logging.LogFormatter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
        LogFormatter logFormatter;
        private String serverIpAddress ;
        private final Logger logger = LogManager.getLogger((HealthEndpoint.class.getName()));


        public HealthEndpoint(Map<String, Integer> metrics) {
            this.metrics = metrics;
            logFormatter = new LogFormatter("noAuth", "noAuth");
            try{
                serverIpAddress = InetAddress.getLocalHost().getHostAddress();
            }catch(UnknownHostException e){
                e.printStackTrace();
            }
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String secLogSigId = logFormatter.formatSignatureId("GET", null);
            String secLogExts = logFormatter.formatExtensions(System.currentTimeMillis(), "GET", "NO USER", secLogSigId, "200", "OK", serverIpAddress);
            String secLog = logFormatter.format("/health", "GET", secLogExts);
            logger.info(secLog);

            httpExchange.sendResponseHeaders(200, 0);

            httpExchange.getResponseBody().write( (new Gson()).toJson(metrics).getBytes());
            httpExchange.getResponseBody().close();
        }
    }
}
