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
            Map<Hand, Integer> handBidMap = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                Hand hand = new Hand(tokens[0]);
                int bid = Integer.parseInt(tokens[1]);
                handBidMap.put(hand, bid);
            }
            List<Hand> sortedHands = handBidMap.keySet().stream().sorted().toList();
            int total = 0;
            int rank = 0;
            for (Hand hand : sortedHands) {
                rank++;
                LOG.debug("Hand: {} - Type: {} - Rank: {}", hand, hand.type(), rank);
                total += handBidMap.get(hand) * rank;
            }
            LOG.info("Output: {}", total);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

    }

    private record Hand(String cards) implements Comparable<Hand> {

        private HandType type() {
            Map<Character, Integer> weightedCards = cards.chars()
                    .mapToObj(s -> (char) s)
                    .collect(Collectors.groupingBy(i -> i, Collectors.summingInt(i -> 1)));
            if (weightedCards.containsKey('J')) {
                int jokerCount = weightedCards.get('J');
                weightedCards.remove('J');
                if (weightedCards.isEmpty()) {
                    return HandType.FIVE_OF_A_KIND;
                }
                char maxChar = Collections.max(weightedCards.entrySet(), Map.Entry.comparingByValue()).getKey();
                int maxCount = weightedCards.get(maxChar);
                weightedCards.replace(maxChar, maxCount + jokerCount);
            }
            int maxCount = weightedCards.values().stream().mapToInt(i -> i).max().orElse(0);
            return switch (maxCount) {
                case 5 -> HandType.FIVE_OF_A_KIND;
                case 4 -> HandType.FOUR_OF_A_KIND;
                case 3 -> {
                    if (weightedCards.containsValue(2)) {
                        yield HandType.FULL_HOUSE;
                    } else {
                        yield HandType.THREE_OF_A_KIND;
                    }
                }
                case 2 -> {
                    if (weightedCards.keySet().size() == 3) {
                        yield HandType.TWO_PAIR;
                    } else {
                        yield HandType.ONE_PAIR;
                    }
                }
                default -> HandType.HIGH_CARD;
            };
        }

        private int typeValue() {
            HandType handType = type();
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

        private int cardValue(int index) {
            char card = cards.charAt(index);
            return switch (card) {
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
            int thisTypeValue = typeValue();
            int otherTypeValue = o.typeValue();
            if (thisTypeValue != otherTypeValue) {
                return thisTypeValue - otherTypeValue;
            }
            int thisFirst = cardValue(0);
            int otherFirst = o.cardValue(0);
            if (thisFirst != otherFirst) {
                return thisFirst - otherFirst;
            }
            int thisSecond = cardValue(1);
            int otherSecond = o.cardValue(1);
            if (thisSecond != otherSecond) {
                return thisSecond - otherSecond;
            }
            int thisThird = cardValue(2);
            int otherThird = o.cardValue(2);
            if (thisThird != otherThird) {
                return thisThird - otherThird;
            }
            int thisFourth = cardValue(3);
            int otherFourth = o.cardValue(3);
            if (thisFourth != otherFourth) {
                return thisFourth - otherFourth;
            }
            int thisFifth = cardValue(4);
            int otherFifth = o.cardValue(4);
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
