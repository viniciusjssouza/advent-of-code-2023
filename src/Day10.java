import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

public class Day10 {

    static final boolean DEBUG = false;

    enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    record Move(Position next, Direction direction) {
    }

    sealed interface Pipe permits NorthSouth, EastWest, NorthEast, NorthWest, SouthEast, SouthWest, MoveNotAllowed {
        static Pipe forChar(char aChar) {
            return switch (aChar) {
                case '|' -> new NorthSouth();
                case '-' -> new EastWest();
                case 'F' -> new NorthEast();
                case '7' -> new NorthWest();
                case 'L' -> new SouthEast();
                case 'J' -> new SouthWest();
                default -> new MoveNotAllowed();
            };
        }

        Optional<Move> move(Position from, Direction direction);
    }

    record MoveNotAllowed() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return Optional.empty();
        }
    }

    // Char |
    record NorthSouth() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return switch (direction) {
                case NORTH ->
                        Optional.of(new Move(new Position(from.row - 1, from.col), Direction.NORTH));
                case SOUTH ->
                        Optional.of(new Move(new Position(from.row + 1, from.col), Direction.SOUTH));
                default -> Optional.empty();
            };
        }
    }

    // Char -
    record EastWest() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return switch (direction) {
                case EAST ->
                        Optional.of(new Move(new Position(from.row, from.col + 1), Direction.EAST));
                case WEST ->
                        Optional.of(new Move(new Position(from.row, from.col - 1), Direction.WEST));
                default -> Optional.empty();
            };
        }
    }

    // Char F
    record NorthEast() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return switch (direction) {
                case NORTH ->
                        Optional.of(new Move(new Position(from.row, from.col + 1), Direction.EAST));
                case WEST ->
                        Optional.of(new Move(new Position(from.row + 1, from.col), Direction.SOUTH));
                default -> Optional.empty();
            };
        }
    }

    // Char 7
    record NorthWest() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return switch (direction) {
                case NORTH ->
                        Optional.of(new Move(new Position(from.row, from.col - 1), Direction.WEST));
                case EAST ->
                        Optional.of(new Move(new Position(from.row + 1, from.col), Direction.SOUTH));
                default -> Optional.empty();
            };
        }
    }

    // Char L
    record SouthEast() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return switch (direction) {
                case SOUTH ->
                        Optional.of(new Move(new Position(from.row, from.col + 1), Direction.EAST));
                case WEST ->
                        Optional.of(new Move(new Position(from.row - 1, from.col), Direction.NORTH));
                default -> Optional.empty();
            };
        }
    }

    // Char J
    record SouthWest() implements Pipe {
        @Override
        public Optional<Move> move(Position from, Direction direction) {
            return switch (direction) {
                case SOUTH ->
                        Optional.of(new Move(new Position(from.row, from.col - 1), Direction.WEST));
                case EAST ->
                        Optional.of(new Move(new Position(from.row - 1, from.col), Direction.NORTH));
                default -> Optional.empty();
            };
        }
    }

    private record Position(int row, int col) {
    }

    public static class PipesGrid {
        private final char[][] grid;
        private final Set<Position> visited = new HashSet<>();
        private final Map<Position, Position> previous = new HashMap<>();
        private final Set<Position> cycle = new HashSet<>();

        public PipesGrid(char[][] grid) {
            this.grid = grid;
        }

        public int countEnclosed() {
            var gridWithOnlyCycle = onlyCycleGrid();
            var startingPoint = this.findStartingPoint();
            debug("Starting position: " + startingPoint);
            var initialPipe = guessInitialPos(startingPoint);
            gridWithOnlyCycle[startingPoint.row][startingPoint.col] = initialPipe;
            return this.runScanline(gridWithOnlyCycle);
        }

        private int runScanline(char[][] grid) {
            int total = 0;
            for (int i = 0; i < grid.length; i++) {
                var inside = false;
                var previous = ' ';
                for (int k = 0; k < grid[i].length; k++) {
                    if (grid[i][k] == '.' && inside) {
                        grid[i][k] = '#';
                        total++;
                    } else if (grid[i][k] == 'F' || grid[i][k] == 'L') {
                        previous = grid[i][k];
                    } else if (grid[i][k] == '|') {
                        inside = !inside;
                    } else if (grid[i][k] == '7' && previous == 'L') {
                        inside = !inside;
                    } else if (grid[i][k] == 'J' && previous == 'F') {
                        inside = !inside;
                    }
                    debug("%c", grid[i][k]);
                }
                debug("\n");
            }
            return total;
        }

        private char guessInitialPos(Position startingPoint) {
            final var rightMove = List.of('7', 'J', '-');
            final var downMove = List.of('J', 'L', '|');
            final var leftMove = List.of('F', 'L', '-');
            final var upMove = List.of('F', '7', '|');

            if (startingPoint instanceof Position(int row, int col)) {
                if (col + 1 < grid[0].length && row + 1 < grid.length
                        && rightMove.contains(grid[row][col + 1]) && downMove.contains(grid[row + 1][col])) {
                    return 'F';
                } else if (col - 1 >= 0 && row + 1 < grid.length
                        && leftMove.contains(grid[row][col - 1]) && downMove.contains(grid[row + 1][col])) {
                    return '7';
                } else if (col - 1 < grid[0].length && row - 1 >= 0
                        && leftMove.contains(grid[row][col - 1]) && upMove.contains(grid[row - 1][col])) {
                    return 'J';
                } else if (col + 1 < grid[0].length && row - 1 >= 0
                        && rightMove.contains(grid[row][col + 1]) && upMove.contains(grid[row - 1][col])) {
                    return 'L';
                } else if (row - 1 >= 0 && row + 1 < grid.length
                        && upMove.contains(grid[row - 1][col]) && downMove.contains(grid[row + 1][col])) {
                    return '|';
                } else {
                    return '-';
                }
            }
            throw new RuntimeException("Invalid starting point");
        }

        private char[][] onlyCycleGrid() {
            var gridWithOnlyCycle = new char[grid.length][grid[0].length];
            for (int i = 0; i < grid.length; i++) {
                for (int k = 0; k < grid[i].length; k++) {
                    if (cycle.contains(new Position(i, k))) {
                        gridWithOnlyCycle[i][k] = grid[i][k];
                    } else {
                        gridWithOnlyCycle[i][k] = '.';
                    }
                }
            }
            return gridWithOnlyCycle;
        }

        public int cycleLength() {
            var startPos = findStartingPoint();
            debug("Starting position: " + startPos);

            var maxCycleLength = IntStream.of(
                    walk(startPos, new Move(new Position(startPos.row - 1, startPos.col), Direction.NORTH)),
                    walk(startPos, new Move(new Position(startPos.row + 1, startPos.col), Direction.SOUTH)),
                    walk(startPos, new Move(new Position(startPos.row, startPos.col - 1), Direction.WEST)),
                    walk(startPos, new Move(new Position(startPos.row, startPos.col + 1), Direction.EAST))
            ).max().getAsInt();

            return maxCycleLength / 2;
        }

        private int walk(Position start, Move move) {
            visited.clear();
            visited.add(start);
            var length = 1;
            var nextMove = move;
            previous.put(nextMove.next, start);
            while (true) {
                var currPosition = nextMove.next;
                if (currPosition.equals(start)) {
                    buildPath(currPosition);
                }
                if (!validPosition(currPosition)) {
                    return 0;
                }
                if (visited.contains(currPosition)) {
                    return length + 1;
                }
                debug("Move: " + currPosition);

                visited.add(currPosition);
                var currChar = grid[currPosition.row][currPosition.col];
                var currPipe = Pipe.forChar(currChar);
                var next = currPipe.move(currPosition, nextMove.direction);
                length++;
                if (next.isEmpty()) {
                    return 0;
                }
                previous.put(next.get().next, currPosition);
                nextMove = next.get();
            }
        }

        private void buildPath(Position start) {
            var curr = start;
            this.cycle.clear();
            do {
                cycle.add(curr);
                curr = previous.get(curr);
            } while (!curr.equals(start));
        }

        private boolean validPosition(Position curr) {
            if (curr.col < 0 || curr.row < 0) {
                return false;
            }
            return curr.row < grid.length && curr.col < grid[curr.row].length;
        }


        private Position findStartingPoint() {
            for (int i = 0; i < grid.length; i++) {
                for (int k = 0; k < grid[i].length; k++) {
                    if (grid[i][k] == 'S') {
                        return new Position(i, k);
                    }
                }
            }
            throw new RuntimeException("Starting point not found");
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            for (var row : grid) {
                sb.append(row).append("\n");
            }
            return sb.toString();
        }
    }

    public static void main(String... args) {
        var grid = new PipesGrid(readGrid());
        debug(grid.toString());

        System.out.println("Part 1: " + grid.cycleLength());
        System.out.println("Part 2: " + grid.countEnclosed());
    }

    public static char[][] readGrid() {
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
        }
    }
}
