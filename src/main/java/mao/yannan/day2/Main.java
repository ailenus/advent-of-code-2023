package mao.yannan.day2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day2.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            LOG.debug("Starting program");
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

}
