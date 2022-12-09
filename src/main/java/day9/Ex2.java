package day9;

import common.Dir2d;
import common.FileParser;
import common.Grid2d;
import common.Vec2d;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Ex2 {
    private static final String DIR = "dir";
    private static final String MOVES = "moves";
    private static final String MOVE_FORMAT = format("(?<%s>\\w) (?<%s>\\d+)", DIR, MOVES);
    private static final Pattern MOVE_PATTERN = Pattern.compile(MOVE_FORMAT);

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input09.txt");

        Grid2d<Integer> visitedCount = new Grid2d<>();
        Set<Vec2d> visited = new HashSet<>();
        Rope rope = new Rope(10);
        Function<Optional<Integer>, Integer> markTailVisit = countOpt -> countOpt.orElse(0) + 1;
        visitedCount.putOrModify(rope.getTail(), markTailVisit);

        for (String line : input) {
            Matcher matcher = MOVE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new Exception("Unhandled input");
            }
            Dir2d dir = Dir2d.fromChar(matcher.group(DIR).charAt(0));
            int moves = Integer.parseInt(matcher.group(MOVES));

            for (int i = 0; i < moves; ++i) {
                rope.moveInDir(dir);
                // FIXME: This is a very bad workaround for a bug somewhere that I'm too tired to look for
                visited.add(new Vec2d(rope.getTail()));
            }
        }

        System.out.println(visited.size());
        System.out.println(visitedCount.numberOfElements());
    }

    private static class Rope {
        private final List<Vec2d> knots = new LinkedList<>();

        public Rope(int knotsCount) {
            for (int i = 0; i < knotsCount; ++i)
                knots.add(new Vec2d(0, 0));
        }

        public void moveInDir(Dir2d dir) {
            Vec2d followed = knots.get(knots.size() - 1);
            Vec2d oldPos = new Vec2d(followed);
            followed.moveInDir(dir);
            for (int i = knots.size() - 2; i >= 0; --i) {
                Vec2d lastMove = oldPos.getDirTo(followed);
                if (lastMove.equals(new Vec2d(0, 0)))
                    break;
                Vec2d following = knots.get(i);
                Vec2d destCandidate = new Vec2d(oldPos);
                oldPos = new Vec2d(following);
                if (!following.touches(followed)) {
                    if (lastMove.isDiagonal()) {
                        Vec2d moveVec = lastMove;
                        if (following.sameAxis(followed))
                            moveVec = following.getDirTo(followed);
                        following.update(following.add(moveVec));
                    } else {
                        following.update(destCandidate);
                    }
                }
                followed = following;
            }
        }

        public Vec2d getHead() {
            return this.knots.get(this.knots.size() - 1);
        }

        public Vec2d getTail() {
            return this.knots.get(0);
        }

        @Override
        public String toString() {
            return String.join(",", knots.stream().map(Vec2d::toString).collect(Collectors.toList()));
        }
    }
}
