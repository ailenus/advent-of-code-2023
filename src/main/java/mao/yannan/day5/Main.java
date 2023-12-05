package mao.yannan.day5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        partOne();
        partTwo();

    }

    private static void partOne() {
        String url = "src/main/resources/day5.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            Set<Long> seeds = new HashSet<>();
            if ((line = reader.readLine()) != null) {
                while (!line.isBlank()) {
                    String[] tokens = line.split("\\s+");
                    Arrays.stream(tokens)
                            .filter(s -> s.matches("\\d+"))
                            .mapToLong(Long::parseLong)
                            .forEach(seeds::add);
                    line = reader.readLine();
                }
            }
            Set<Long> soils = map(seeds, reader);
            Set<Long> fertilizers = map(soils, reader);
            Set<Long> waters = map(fertilizers, reader);
            Set<Long> lights = map(waters, reader);
            Set<Long> temperatures = map(lights, reader);
            Set<Long> humidities = map(temperatures, reader);
            Set<Long> locations = map(humidities, reader);
            long minLocation = Collections.min(locations);
            LOG.info("Part 1 output: {}", minLocation);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static void partTwo() {
        String url = "src/main/resources/day5.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line = reader.readLine().substring("seeds: ".length());
            TreeSet<Range> ranges = new TreeSet<>();
            List<Long> seeds = Arrays.stream(line.split("\\s+")).map(Long::parseLong).toList();
            for (int i = 0; i < seeds.size() - 1; i += 2) {
                long start = seeds.get(i);
                ranges.add(new Range(start, start + seeds.get(i + 1) - 1));
            }
            mergeRanges(ranges);
            List<CompositeMap> maps = new ArrayList<>();
            CompositeMap current = null;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (current != null) {
                        maps.add(current);
                    }
                    line = reader.readLine();
                    if (!line.isBlank()) {
                        current = new CompositeMap();
                    }
                } else {
                    assert current != null;
                    current.addMap(Arrays.stream(line.split("\\s+")).mapToLong(Long::parseLong).toArray());
                }
            }
            maps.add(current);
            for (CompositeMap map : maps) {
                ranges = map.map(ranges);
            }
            LOG.info("Part 2 output: {}", ranges.getFirst().start);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static Set<Long> map(Set<Long> domain, BufferedReader reader) throws IOException {
        String line;
        Set<Long> image = new HashSet<>(domain);
        if ((line = reader.readLine()) != null) {
            while (line != null && !line.isBlank()) {
                String[] tokens = line.split("\\s+");
                if (isText(tokens[0])) {
                    line = reader.readLine();
                    continue;
                }
                long imageIndex = Long.parseLong(tokens[0]);
                long domainIndex = Long.parseLong(tokens[1]);
                long length = Long.parseLong(tokens[2]);
                for (long value : domain) {
                    if (domainIndex <= value && domainIndex + length - 1 >= value) {
                        image.remove(value);
                        image.add(value - domainIndex + imageIndex);
                    }
                }
                line = reader.readLine();
            }
        }
        return image;
    }

    private static boolean isText(String token) {
        return token != null && !token.isBlank() && token.contains("-");
    }

    private static void mergeRanges(TreeSet<Range> ranges) {
        Range previous = null;
        for (Iterator<Range> iterator = ranges.iterator(); iterator.hasNext(); ) {
            Range current = iterator.next();
            if (previous == null) {
                previous = current;
                continue;
            }
            if (previous.isOverlappedByOrAdjacentTo(current)) {
                previous.start = Math.min(previous.start, current.start);
                previous.end = Math.max(previous.end, current.end);
                iterator.remove();
            } else {
                previous = current;
            }
        }
    }

    private static class CompositeMap {

        private final List<RangeMap> maps = new ArrayList<>();

        private void addMap(long[] fields) {
            maps.add(new RangeMap(fields[0], fields[1], fields[2]));
        }

        private TreeSet<Range> map(TreeSet<Range> ranges) {
            TreeSet<Range> mappedRanges = new TreeSet<>();
            TreeSet<Range> unmappedRanges = ranges;
            for (RangeMap map : maps) {
                if (unmappedRanges.isEmpty()) {
                    break;
                }
                TreeSet<Range> temp = new TreeSet<>();
                map.map(unmappedRanges, temp, mappedRanges);
                unmappedRanges = temp;
            }
            mappedRanges.addAll(unmappedRanges);
            mergeRanges(mappedRanges);
            return mappedRanges;
        }

    }

    private static class RangeMap {

        private final Range domain;
        private final long imageStart;

        private RangeMap(long imageStart, long domainStart, long length) {
            domain = new Range(domainStart, domainStart + length - 1);
            this.imageStart = imageStart;
        }

        private void map(TreeSet<Range> ranges, TreeSet<Range> unmappedRanges, TreeSet<Range> mappedRanges) {
            for (Range r : ranges) {
                if (domain.end < r.start || domain.start > r.end) {
                    unmappedRanges.add(r);
                    continue;
                }
                if (r.start < domain.start) {
                    unmappedRanges.add(new Range(r.start, domain.start - 1));
                }
                if (r.end > domain.end) {
                    unmappedRanges.add(new Range(domain.end + 1, r.end));
                }
                mappedRanges.add(new Range(
                        Math.max(domain.start, r.start) - domain.start + imageStart,
                        Math.min(domain.end, r.end) - domain.start + imageStart)
                );
            }
        }

    }

    private static class Range implements Comparable<Range> {

        private long start;
        private long end;

        private Range(long start, long end) {
            this.start = start;
            this.end = end;
        }

        private boolean isOverlappedByOrAdjacentTo(Range range) {
            return range.contains(start) || range.contains(start - 1) || range.contains(end) || range.contains(end + 1);
        }

        private boolean contains(long l) {
            return l >= start && l <= end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return start == range.start && end == range.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public int compareTo(Range o) {
            long diff = start - o.start;
            if (diff == 0) {
                diff = end - o.end;
            }
            if (diff < 0) {
                return -1;
            }
            if (diff > 0) {
                return 1;
            }
            return 0;
        }

    }

}
