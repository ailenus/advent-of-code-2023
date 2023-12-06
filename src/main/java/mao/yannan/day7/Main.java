package mao.yannan.day7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day7.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line = reader.readLine();
            LOG.info("Line: {}", line);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

}
