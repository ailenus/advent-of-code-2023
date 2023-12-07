package mao.yannan.day7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        String url = "src/main/resources/day7.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            Map<Hand, Integer> hands = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                Hand hand = new Hand(tokens[0]);
                int bid = Integer.parseInt(tokens[1]);
                hands.put(hand, bid);
            }
            List<Hand> sortedHands = hands.keySet().stream().sorted().toList();
            int total = 0;
            int count = 1;
            for (Hand hand : sortedHands) {
                LOG.debug("Hand: {} - Type: {} - Rank: {}", hand, hand.getType(), count);
                total += hands.get(hand) * count;
                count++;
            }
            LOG.info("Output: {}", total);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private record Hand(String cards) implements Comparable<Hand> {

        private HandType getType() {
            Map<Character, Integer> multiset = cards.chars()
                    .mapToObj(s -> (char) s)
                    .collect(Collectors.groupingBy(i -> i, Collectors.summingInt(i -> 1)));
            if (multiset.containsKey('J')) {
                int jokerCount = multiset.get('J');
                multiset.remove('J');
                if (multiset.isEmpty()) {
                    return HandType.FIVE_OF_A_KIND;
                }
                char maxChar = Collections.max(multiset.entrySet(), Map.Entry.comparingByValue()).getKey();
                int maxCount = multiset.get(maxChar);
                multiset.replace(maxChar, maxCount + jokerCount);
            }
            int maxCount = multiset.values().stream().mapToInt(i -> i).max().orElse(0);
            return switch (maxCount) {
                case 5 -> HandType.FIVE_OF_A_KIND;
                case 4 -> HandType.FOUR_OF_A_KIND;
                case 3 -> {
                    if (multiset.containsValue(2)) {
                        yield HandType.FULL_HOUSE;
                    } else {
                        yield HandType.THREE_OF_A_KIND;
                    }
                }
                case 2 -> {
                    if (multiset.keySet().size() == 3) {
                        yield HandType.TWO_PAIR;
                    } else {
                        yield HandType.ONE_PAIR;
                    }
                }
                default -> HandType.HIGH_CARD;
            };
        }

        private int getTypeValue() {
            HandType handType = getType();
            return switch (handType) {
                case FIVE_OF_A_KIND -> 7;
                case FOUR_OF_A_KIND -> 6;
                case FULL_HOUSE -> 5;
                case THREE_OF_A_KIND -> 4;
                case TWO_PAIR -> 3;
                case ONE_PAIR -> 2;
                case HIGH_CARD -> 1;
            };
        }

        private int getCardValue(int index) {
            char first = cards.charAt(index);
            return switch (first) {
                case 'A', 'a' -> 13;
                case 'K', 'k' -> 12;
                case 'Q', 'q' -> 11;
                case 'T', 't' -> 10;
                case '9' -> 9;
                case '8' -> 8;
                case '7' -> 7;
                case '6' -> 6;
                case '5' -> 5;
                case '4' -> 4;
                case '3' -> 3;
                case '2' -> 2;
                default -> 1;
            };
        }

        @Override
        public int compareTo(Hand o) {
            int thisTypeValue = getTypeValue();
            int otherTypeValue = o.getTypeValue();
            if (thisTypeValue != otherTypeValue) {
                return thisTypeValue - otherTypeValue;
            }
            int thisFirst = getCardValue(0);
            int otherFirst = o.getCardValue(0);
            if (thisFirst != otherFirst) {
                return thisFirst - otherFirst;
            }
            int thisSecond = getCardValue(1);
            int otherSecond = o.getCardValue(1);
            if (thisSecond != otherSecond) {
                return thisSecond - otherSecond;
            }
            int thisThird = getCardValue(2);
            int otherThird = o.getCardValue(2);
            if (thisThird != otherThird) {
                return thisThird - otherThird;
            }
            int thisFourth = getCardValue(3);
            int otherFourth = o.getCardValue(3);
            if (thisFourth != otherFourth) {
                return thisFourth - otherFourth;
            }
            int thisFifth = getCardValue(4);
            int otherFifth = o.getCardValue(4);
            if (thisFifth != otherFifth) {
                return thisFifth - otherFifth;
            }
            return 0;
        }

    }

    private enum HandType {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }

}
