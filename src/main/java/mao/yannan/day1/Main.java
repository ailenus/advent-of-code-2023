package mao.yannan.day1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day1.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            int sum = 0;
            int count = 1;
            while ((line = reader.readLine()) != null) {
                LOG.debug("Starting to process line {}", count);
                Pattern pattern = Pattern.compile("\\d|one|two|three|four|five|six|seven|eight|nine");
                Matcher matcher = pattern.matcher(line);
                String match;
                int i;
                if (matcher.find()) {
                    match = matcher.group();
                    LOG.debug("First match: {}", match);
                    i = getInt(match);
                    sum += i * 10;
                }
                pattern = Pattern.compile("\\d|eno|owt|eerht|ruof|evif|xis|neves|thgie|enin");
                matcher = pattern.matcher(new StringBuilder(line).reverse());
                if (matcher.find()) {
                    match = matcher.group();
                    LOG.debug("Last match: {}", match);
                    i = getInt(Objects.requireNonNull(match));
                    sum += i;
                }
                count++;
            }
            LOG.info("Output: {}", sum);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private static int getInt(String match) {
        return switch (match) {
            case "1", "one", "eno" -> 1;
            case "2", "two", "owt" -> 2;
            case "3", "three", "eerht" -> 3;
            case "4", "four", "ruof" -> 4;
            case "5", "five", "evif" -> 5;
            case "6", "six", "xis" -> 6;
            case "7", "seven", "neves" -> 7;
            case "8", "eight", "thgie" -> 8;
            case "9", "nine", "enin" -> 9;
            default -> 0;
        };
    }

}
