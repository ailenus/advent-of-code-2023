package mao.yannan.day4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day4.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            int sum = 0;
            List<Integer> matches = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s*[:|]\\s+");
                int[] winningNumbers = Arrays.stream(tokens[1].split("\\s+"))
                        .mapToInt(Integer::parseInt)
                        .toArray();
                int[] numbers = Arrays.stream(tokens[2].split("\\s+"))
                        .mapToInt(Integer::parseInt)
                        .toArray();
                int match = (int) Arrays.stream(numbers)
                        .filter(i -> Arrays.stream(winningNumbers).anyMatch(j -> j == i))
                        .count();
                int score = (1 << match) / 2;
                sum += score;
                matches.add(match);
            }
            int total = processTotal(matches);
            LOG.info("Part 1 output: {}", sum);
            LOG.info("Part 2 output: {}", total);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private static int processTotal(List<Integer> matches) {
        WeightedList list = new WeightedList(matches);
        for (int i = 0; i < matches.size(); i++) {
            int match = matches.get(i);
            for (int j = 0; j < match; j++) {
                list.incrementWeight(i + j + 1, list.getWeight(i));
            }
        }
        return list.sumWeights();
    }

    private static final class WeightedList {

        private final List<Integer> weights;

        private WeightedList(List<Integer> values) {
            weights = new ArrayList<>(Collections.nCopies(values.size(), 1));
        }

        private void incrementWeight(int index, int weight) {
            weights.set(index, weights.get(index) + weight);
        }

        private int getWeight(int index) {
            return weights.get(index);
        }

        private int sumWeights() {
            return weights.stream().mapToInt(Integer::intValue).sum();
        }

    }

}
