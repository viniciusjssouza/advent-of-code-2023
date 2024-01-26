import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 {

    private static final boolean DEBUG = false;
    private static final char GALAXY = '#';

    private record Position(int row, int col) {
    }

    private record GalaxyPair(Position a, Position b) {
        public BigInteger calcDistance(Set<Integer> rowsWithoutGalaxies, Set<Integer> colsWithoutGalaxies, long expansionFactor) {
            final var rowDistance = BigInteger.valueOf(Math.abs(a.row() - b.row())).add(
                    distanceDueToExpansion(expansionFactor, rowsWithoutGalaxies, a.row(), b.row()));
            final var colDistance = BigInteger.valueOf(Math.abs(a.col() - b.col())).add(
                    distanceDueToExpansion(expansionFactor, colsWithoutGalaxies, a.col(), b.col()));
            return rowDistance.add(colDistance);
        }

        private BigInteger distanceDueToExpansion(long expansionFactor, Set<Integer> expandedRegion, int a, int b) {
            var expansionCount = IntStream.rangeClosed(Math.min(a, b), Math.max(a, b))
                    .filter(expandedRegion::contains)
                    .count();
            return BigInteger.valueOf(expansionCount).multiply(BigInteger.valueOf(expansionFactor));
        }
    }

    public record UniverseImage(char[][] image) {
        public List<Position> allGalaxies() {
            var allGalaxies = new ArrayList<Position>();
            for (int row = 0; row < image.length; row++) {
                for (int col = 0; col < image[row].length; col++) {
                    if (image[row][col] == GALAXY) {
                        allGalaxies.add(new Position(row, col));
                    }
                }
            }
            return allGalaxies;
        }

        public List<GalaxyPair> allPairsOfGalaxies() {
            var all = allGalaxies();
            var pairs = new ArrayList<GalaxyPair>();
            for (int i = 0; i < all.size(); i++) {
                for (int j = i + 1; j < all.size(); j++) {
                    pairs.add(new GalaxyPair(all.get(i), all.get(j)));
                }
            }
            return pairs;
        }

        @Override
        public String toString() {
            return Arrays.deepToString(image).replaceAll("],", "]\n");
        }

        public Set<Integer> rowsWithoutGalaxies() {
            final var rowsWithGalaxies = allGalaxies()
                    .stream().map(Position::row).collect(Collectors.toSet());
            return IntStream.range(0, image.length).
                    filter(row -> !rowsWithGalaxies.contains(row)).
                    boxed().
                    collect(Collectors.toSet());
        }

        public Set<Integer> colsWithoutGalaxies() {
            final var colsWithoutGalaxies = allGalaxies()
                    .stream().map(Position::col).collect(Collectors.toSet());
            return IntStream.range(0, image[0].length).
                    filter(row -> !colsWithoutGalaxies.contains(row)).
                    boxed().
                    collect(Collectors.toSet());
        }
    }

    public static void main(String... args) {
        UniverseImage universeImage = new UniverseImage(readImage());
        debug(universeImage.toString());
        final var rowsWithoutGalaxies = universeImage.rowsWithoutGalaxies();
        final var colsWithoutGalaxies = universeImage.colsWithoutGalaxies();
        debug("rowsWithoutGalaxies: %s", rowsWithoutGalaxies);
        debug("colsWithoutGalaxies: %s", colsWithoutGalaxies);
        var sumOfDistances = universeImage.
                allPairsOfGalaxies().stream()
                .map(pair -> pair.calcDistance(rowsWithoutGalaxies, colsWithoutGalaxies, 1L))
                .reduce(BigInteger::add).orElseThrow(() -> new RuntimeException("No pairs found"));
        System.out.println("part1:" + sumOfDistances);

        sumOfDistances = universeImage.
                allPairsOfGalaxies().stream()
                .map(pair -> pair.calcDistance(rowsWithoutGalaxies, colsWithoutGalaxies, 999_999L))
                .reduce(BigInteger::add).orElseThrow(()-> new RuntimeException("No pairs found"));
        System.out.println("part2:" + sumOfDistances);
    }

    public static char[][] readImage() {
        try {
            var grid = new ArrayList<char[]>();
            var scanner = new Scanner(new FileInputStream("./src/input.txt"));
            while (scanner.hasNext()) {
                grid.add(scanner.nextLine().toCharArray());
            }
            return grid.toArray(new char[0][]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void debug(String msg, Object... args) {
        if (DEBUG) {
            System.out.printf(msg, args);
            System.out.println();
        }
    }
}
