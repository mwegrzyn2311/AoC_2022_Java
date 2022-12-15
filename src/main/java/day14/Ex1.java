package day14;

import common.Dir2d;
import common.FileParser;
import common.Grid2d;
import common.Vec2d;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class Ex1 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input14.txt");

        Cave cave = Cave.fromInput(input);

        System.out.println(cave);
        System.out.println(cave.simulateFalling() - 1);
        System.out.println(cave);
    }

    static class Cave extends Grid2d<CaveEle> {
        private static final Vec2d SAND_ORIGIN = new Vec2d(500, 0);
        private Sand currentlyFalling;
        private int producedSand = 0;
        private int lowestRock;

        public static Cave fromInput(List<String> input) throws Exception {
            Cave cave = new Cave();
            for (String line : input) {
                List<Vec2d> posSeq = Arrays.stream(line.split(" -> "))
                        .map(Vec2d::fromString)
                        .collect(Collectors.toList());
                for (int i = 1; i < posSeq.size(); ++i) {
                    Vec2d.createVecLine(posSeq.get(i - 1), posSeq.get(i))
                            .stream()
                            .filter(pos -> !cave.hasEleAt(pos))
                            .forEach(pos -> cave.add(pos, new Rock(pos)));
                }
            }
            cave.calculateLowestRock();
            return cave;
        }

        public void produceSand() {
            currentlyFalling = new Sand(SAND_ORIGIN);
            ++producedSand;
        }

        public int simulateFalling() {
            while(isNull(currentlyFalling) || currentlyFalling.getPos().getY() <= lowestRock)
                fallStep();
            return producedSand;
        }

        public void fallStep() {
            if (isNull(currentlyFalling)) {
                produceSand();
            }
            Optional<Vec2d> nextFallingPos = getNextFallingPos();
            if (nextFallingPos.isPresent()) {
                moveEle(currentlyFalling, nextFallingPos.get());
            } else {
                currentlyFalling = null;
            }
        }

        public void moveEle(CaveEle ele, Vec2d newPos) {
            grid.remove(ele.getPos(), ele);
            currentlyFalling.setPos(newPos);
            grid.put(ele.getPos(), ele);
        }

        public void calculateLowestRock() {
            this.lowestRock = grid.entrySet().stream()
                    .filter(es -> es.getValue() instanceof Rock)
                    .map(Map.Entry::getKey)
                    .mapToInt(Vec2d::getY)
                    .max()
                    .orElse(SAND_ORIGIN.getY());
        }

        /**
         * Be carefull - it this ex UP is actually DOWN because of axis differences
         */
        public Optional<Vec2d> getNextFallingPos() {
            Vec2d curr = currentlyFalling.getPos();
            if (!hasEleAt(curr.inDir(Dir2d.UP))) {
                return Optional.of(curr.inDir(Dir2d.UP));
            } else if (!hasEleAt(curr.add(new Vec2d(-1, 1)))) {
                return Optional.of(curr.add(new Vec2d(-1, 1)));
            } else if (!hasEleAt(curr.add(new Vec2d(1, 1)))) {
                return Optional.of(curr.add(new Vec2d(1, 1)));
            } else {
                return Optional.empty();
            }
        }
    }

    static class Rock extends CaveEle {
        public Rock(Vec2d pos) {
            super(pos);
        }

        @Override
        public String toString() {
            return "#";
        }
    }

    static class Sand extends CaveEle {
        public Sand(Vec2d pos) {
            super(pos);
        }

        @Override
        public String toString() {
            return "O";
        }
    }

    static abstract class CaveEle {
        protected Vec2d pos;

        public CaveEle(Vec2d pos) {
            this.pos = pos;
        }

        public Vec2d getPos() {
            return pos;
        }

        public void setPos(Vec2d pos) {
            this.pos = pos;
        }
    }
}
