package mao.yannan.day3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day3.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            int lineNumber = 0;
            Set<Number> numbers = new HashSet<>();
            Set<Symbol> symbols = new HashSet<>();
            Pattern numberPattern = Pattern.compile("\\d+");
            Pattern symbolPattern = Pattern.compile("[^\\d.]");
            while ((line = reader.readLine()) != null) {
                Matcher numberMatcher = numberPattern.matcher(line);
                while (numberMatcher.find()) {
                    int start = numberMatcher.start();
                    int end = numberMatcher.end();
                    int value = Integer.parseInt(numberMatcher.group());
                    numbers.add(new Number(value, start, end - 1, lineNumber));
                }
                Matcher symbolMatcher = symbolPattern.matcher(line);
                while (symbolMatcher.find()) {
                    int start = symbolMatcher.start();
                    symbols.add(new Symbol(start, lineNumber));
                }
                lineNumber++;
            }
            AtomicInteger result1 = new AtomicInteger();
            numbers.stream()
                    .filter(number -> number.isAdjacent(symbols))
                    .forEach(number -> result1.addAndGet(number.value));
            LOG.info("Part 1 output: {}", result1.get());
            AtomicInteger result2 = new AtomicInteger();
            symbols.forEach(symbol -> result2.addAndGet(symbol.getRatio(numbers)));
            LOG.info("Part 2 output: {}", result2.get());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private record Number(int value, int startAbscissa, int endAbscissa, int ordinate) {

        private boolean isAdjacent(Set<Symbol> symbols) {
            return symbols.stream().anyMatch(this::isAdjacent);
        }

        private boolean isAdjacent(Symbol symbol) {
            return ordinate - 1 <= symbol.ordinate
                && ordinate + 1 >= symbol.ordinate
                && startAbscissa - 1 <= symbol.abscissa
                && endAbscissa + 1 >= symbol.abscissa;
        }

    }

    private record Symbol(int abscissa, int ordinate) {

        private int getRatio(Set<Number> numbers) {
            Set<Number> neighbors = numbers.stream()
                    .filter(number -> number.isAdjacent(this))
                    .collect(Collectors.toSet());
            if (neighbors.size() != 2) {
                return 0;
            }
            AtomicInteger ratio = new AtomicInteger(1);
            neighbors.forEach(number -> ratio.updateAndGet(v -> v * number.value));
            return ratio.get();
        }

    }

}
