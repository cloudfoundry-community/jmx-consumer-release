package helpers;

import integration.AcceptanceTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class OutputLogRedirector {
    public OutputLogRedirector() {
    }

    public void writeLogsToStdout(Process process) {
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.out::println);

        new Thread(outputGobbler).start();
        new Thread(errorGobbler).start();
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
