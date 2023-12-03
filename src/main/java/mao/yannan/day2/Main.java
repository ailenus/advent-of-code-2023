package mao.yannan.day2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    private static final int MAX_RED = 12;
    private static final int MAX_GREEN = 13;
    private static final int MAX_BLUE = 14;

    private static int minRed;
    private static int minGreen;
    private static int minBlue;

    public static void main(String[] args) {

        String url = "src/main/resources/day2.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            int sum = 0;
            int power = 0;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(":\\s*");
                String[] game = fields[1].split(";*,*\\s+");
                if (isPossible(game)) {
                    int index = Integer.parseInt(fields[0].substring(5));
                    sum += index;
                }
                processMin(game);
                power += minRed * minGreen * minBlue;
                minRed = 0;
                minGreen = 0;
                minBlue = 0;
            }
            LOG.info("Part 1 output: {}", sum);
            LOG.info("Part 2 output: {}", power);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private static boolean isPossible(String[] game) {
        if (game.length == 2) {
            int count = Integer.parseInt(game[0]);
            String color = game[1];
            return switch (color) {
                case "red" -> count <= MAX_RED;
                case "green" -> count <= MAX_GREEN;
                case "blue" -> count <= MAX_BLUE;
                default -> false;
            };
        } else {
            String[] firstTrial = Arrays.copyOfRange(game, 0, 2);
            String[] latter = Arrays.copyOfRange(game, 2, game.length);
            return isPossible(firstTrial) && isPossible(latter);
        }
    }

    private static void processMin(String[] game) {
        if (game.length == 2) {
            int count = Integer.parseInt(game[0]);
            String color = game[1];
            switch (color) {
                case "red":
                    if (count > minRed) {
                        minRed = count;
                    }
                    break;
                case "green":
                    if (count > minGreen) {
                        minGreen = count;
                    }
                    break;
                case "blue":
                    if (count > minBlue) {
                        minBlue = count;
                    }
                    break;
                default:
                    LOG.error("Should not be here");
            }
        } else {
            String[] firstTrial = Arrays.copyOfRange(game, 0, 2);
            String[] latter = Arrays.copyOfRange(game, 2, game.length);
            processMin(firstTrial);
            processMin(latter);
        }
    }

}
