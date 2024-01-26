import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class Day09 {

    private static final boolean DEBUG = false;

    public record ValueHistory(long[] values) {
        public long extrapolateNextValue(BiFunction<long[], Integer, Long> valueSelector,  BinaryOperator<Long> reducer) {
            var currValues = Arrays.copyOf(values, values.length);
            var selectedValues = new LinkedList<Long>();
            var level = 0;
            selectedValues.addFirst(valueSelector.apply(currValues, level));
            do {
                nextLevel(currValues, level);
                level++;
                selectedValues.addFirst(valueSelector.apply(currValues, level));
            } while (!isAllZeros(currValues, level));
            if (DEBUG) {
                System.out.println("Last Values: " + selectedValues);
            }
            return selectedValues.stream().reduce(0L, reducer);
        }

        private void nextLevel(long[] currValues, int level) {
            for (int i = 0; i+1 < currValues.length-level; i++) {
                currValues[i] = currValues[i+1] - currValues[i];
            }
        }

        private boolean isAllZeros(long[] currentValues, int level) {
            for (int i = 0; i < currentValues.length-level; i++) {
                if (currentValues[i] != 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
           return Arrays.toString(values);
        }
    }

    public static void main(String[] args) {
        Day09 day09 = new Day09();
        var lines = day09.readLines().toList();

        var part1 = lines.stream().map(day09::toValueHistory)
            .mapToLong(v -> {
                if (DEBUG)
                    System.out.println(v);
                return v.extrapolateNextValue(
                        (values, level) -> values[values.length-1-level],
                        Long::sum
                );
            }).sum();
        System.out.println("Part 1: " + part1);

        var part2 = lines.stream().map(day09::toValueHistory)
                .mapToLong(v -> {
                    var val = v.extrapolateNextValue(
                            (values, level) -> values[0],
                            (acc, curr) -> curr - acc
                    );
                    if (DEBUG)
                        System.out.println(v + " -> " + val);
                    return val;
                }).sum();
        System.out.println("Part 2: " + part2);
    }

    public ValueHistory toValueHistory(String line)  {
        return new ValueHistory(
            Stream.of(line.split("\\s"))
                .mapToLong(Long::parseLong)
                .toArray()
        );
    }

    public Stream<String> readLines()  {
        try {
            return new BufferedReader(new FileReader("./src/input.txt")).lines();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
