package day17;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.Dir2d;
import common.FileParser;
import common.Vec2d;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static day17.Ex2.TetrisRockType.*;
import static java.util.Objects.isNull;

public class Ex2 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input17.txt");

        TetrisCave cave = TetrisCave.fromString(input.get(0));

        System.out.println(cave.simulateHeightAfterSomeRocks(10000L));
    }

    static class TetrisCave {
        private static final int LEFT_WALL_X = -3;
        private static final int RIGHT_WALL_X = 5;
        private static final int NEW_ROCK_Y_OFFSET = 3;
        private static final List<TetrisRockType> rockTypesSeq = ImmutableList.of(WORM, PLUS, FLIPPED_L, I, SQUARE);
        private final List<Dir2d> pushSeq;
        //private final Map<Integer, Integer> maxYbyX = IntStream.range(LEFT_WALL_X, RIGHT_WALL_X).boxed().collect(Collectors.toMap(Function.identity(), x -> 0));
        private final Set<Vec2d> occupied = new HashSet<>();
        private long rocksFallen = 0;
        private int currPush = 0;
        private TetrisRock curr = null;
        private int prevMaxHeight = 0;
        
        public int simulateHeightAfterSomeRocks(long rocks) {
            while (rocksFallen < rocks) {
                step();
            }
            return maxHeight();
        }

        public void step() {
            if (isNull(curr)) {
                if (rocksFallen % 10000L == 0) {
                    System.out.printf("%d rocks fallen.%n", rocksFallen);
                }
                spawnNewRock();
            }
            Dir2d pushDir = nextPush();
            if (canMove(pushDir)) {
                move(pushDir);
            }
            if (canMove(Dir2d.DOWN)) {
                move(Dir2d.DOWN);
            } else {
                if (rocksFallen % rockTypesSeq.size() == 0 && currPush == 4 && curr.getSegments().stream()
                        .mapToInt(Vec2d::getX)
                        .boxed()
                        .collect(Collectors.toList())
                        .containsAll(ImmutableList.of(0, 1, 2, 3))) {
                    System.out.println("PATTERN???");
                }
                curr = null;
                optimizeOccupied();
                ++rocksFallen;
                int newMaxHeight = maxHeight();
                System.out.printf("New max = %d, diff = %d - after %d.%n", newMaxHeight, newMaxHeight - prevMaxHeight, rocksFallen);
                prevMaxHeight = newMaxHeight;
            }
        }

        private void optimizeOccupied() {
            occupied.removeIf(pos -> pos.getY() < minMaxHeight());
        }

        private void move(Dir2d dir) {
            occupied.removeAll(curr.getSegments());
            occupied.addAll(curr.move(dir));
        }

        private void spawnNewRock() {
            curr = new TetrisRock(rockTypesSeq.get((int) (rocksFallen % rockTypesSeq.size())), new Vec2d(0, maxHeight() + NEW_ROCK_Y_OFFSET));
            occupied.addAll(curr.getSegments());
        }

        private boolean canMove(Dir2d dir) {
            Set<Vec2d> segmentsIfMove = curr.segmentsIfMove(dir);
            return noRockCollision(segmentsIfMove) && noWallCollision(segmentsIfMove);
        }

        private boolean noWallCollision(Set<Vec2d> segmentsIfMove) {
            return segmentsIfMove.stream().allMatch(pos -> pos.getX() > LEFT_WALL_X && pos.getX() < RIGHT_WALL_X && pos.getY() >= 0);
        }

        private boolean noRockCollision(Set<Vec2d> segmentsIfMove) {
            return occupied.stream()
                    .filter(ele -> !curr.getSegments().contains(ele))
                    .noneMatch(segmentsIfMove::contains);
        }

        private Dir2d nextPush() {
            Dir2d res =  pushSeq.get(currPush);
            currPush = (currPush + 1) % pushSeq.size();
            return res;
        }

        private int maxHeight() {
            return occupied.stream()
                    .mapToInt(Vec2d::getY)
                    .max().orElse(-1) + 1;
        }

        private int minMaxHeight() {
            return occupied.stream()
                    .collect(Collectors.groupingBy(Vec2d::getX)).values().stream()
                    .mapToInt(posList -> posList.stream().mapToInt(Vec2d::getY).max().orElse(-1))
                    .min().orElse(0);
        }

        public static TetrisCave fromString(String pushSeqStr) {
            return new TetrisCave(pushSeqStr.chars()
                    .mapToObj(c -> switch(c) {
                        case (int) '<' -> Dir2d.LEFT;
                        case (int) '>' -> Dir2d.RIGHT;
                        default -> null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        public TetrisCave(List<Dir2d> pushSeq) {
            this.pushSeq = pushSeq;
        }

        @Override
        public String toString() {
            return IntStream.range(-1, maxHeight())
                    .boxed()
                    .sorted(Collections.reverseOrder())
                    .map(y -> IntStream.range(LEFT_WALL_X, RIGHT_WALL_X + 1)
                            .mapToObj(x -> eleViz(new Vec2d(x, y)))
                            .collect(Collectors.joining()))
                    .collect(Collectors.joining("\n"));
        }

        private String eleViz(Vec2d pos) {
            if (pos.getY() == -1) {
                if (pos.getX() == LEFT_WALL_X || pos.getX() == RIGHT_WALL_X)
                    return "+";
                else
                    return "-";
            } else if (pos.getX() == LEFT_WALL_X || pos.getX() == RIGHT_WALL_X) {
                return "|";
            } else if (curr.getSegments().contains(pos)) {
                return "@";
            } else if (occupied.contains(pos)) {
                return "#";
            } else {
                return ".";
            }
        }
    }

    static class TetrisRock {
        private Set<Vec2d> segments;

        public Set<Vec2d> segmentsIfMove(Dir2d dir) {
            return segments.stream()
                    .map(pos -> pos.inDir(dir))
                    .collect(Collectors.toSet());
        }

        public Set<Vec2d> move(Dir2d dir) {
            segments = segmentsIfMove(dir);
            return segments;
        }

        public TetrisRock(TetrisRockType type, Vec2d offset) {
            this.segments = type.getRelativeSegments().stream()
                    .map(pos -> pos.add(offset))
                    .collect(Collectors.toSet());
        }

        public Set<Vec2d> getSegments() {
            return segments;
        }
    }

    enum TetrisRockType {
        WORM(ImmutableSet.of(new Vec2d(0, 0), new Vec2d(1, 0), new Vec2d(2, 0), new Vec2d(3, 0))),
        PLUS(ImmutableSet.of(new Vec2d(1, 0), new Vec2d(0, 1), new Vec2d(1, 1), new Vec2d(2, 1), new Vec2d(1, 2))),
        FLIPPED_L(ImmutableSet.of(new Vec2d(0, 0), new Vec2d(1, 0), new Vec2d(2, 0), new Vec2d(2, 1), new Vec2d(2, 2))),
        I(ImmutableSet.of(new Vec2d(0, 0), new Vec2d(0, 1), new Vec2d(0, 2), new Vec2d(0, 3))),
        SQUARE(ImmutableSet.of(new Vec2d(0, 0), new Vec2d(1, 0), new Vec2d(0, 1), new Vec2d(1,1)));

        private final Set<Vec2d> relativeSegments;

        public Set<Vec2d> getRelativeSegments() {
            return relativeSegments;
        }

        TetrisRockType(Set<Vec2d> relativeSegments) {
            this.relativeSegments = relativeSegments;
        }
    }
}
