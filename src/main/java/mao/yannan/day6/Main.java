package mao.yannan.day6;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day6.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line = reader.readLine();
            int[] times = Arrays.stream(line.split("\\D*\\s+"))
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            line = reader.readLine();
            int[] distances = Arrays.stream(line.split("\\D*\\s+"))
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            int length = times.length;
            long product = 1;
            for (int i = 0; i < length; i++) {
                product *= getWays(times[i], distances[i]);
            }
            LOG.info("Part 1 output: {}", product);

            long time = Long.parseLong(Arrays.stream(times)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining("")));
            long distance = Long.parseLong(Arrays.stream(distances)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining("")));
            LOG.info("Part 2 output: {}", getWays(time, distance));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private static long getWays(long sum, long threshold) {
        long half = sum / 2;
        long diff = half * (sum - half) - threshold;
        long dist = (long) Math.sqrt(diff);
        long startTrial = half - dist;
        long cutoff = -1L;
        for (long i = 0L; i < half; i++) {
            long trialCutoff = startTrial - 2 + i;
            if (trialCutoff * (sum - trialCutoff) > threshold) {
                cutoff = trialCutoff;
                break;
            }
        }
        return sum - cutoff * 2 + 1;
    }

}
