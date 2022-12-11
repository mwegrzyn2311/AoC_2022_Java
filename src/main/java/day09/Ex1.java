package day09;

import common.Dir2d;
import common.FileParser;
import common.Grid2d;
import common.Vec2d;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Ex1 {
    private static final String DIR = "dir";
    private static final String MOVES = "moves";
    private static final String MOVE_FORMAT = format("(?<%s>\\w) (?<%s>\\d+)", DIR, MOVES);
    private static final Pattern MOVE_PATTERN = Pattern.compile(MOVE_FORMAT);

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input09.txt");

        Grid2d<Integer> visitedCount = new Grid2d<>();
        Vec2d head = new Vec2d(0, 0);
        Vec2d tail = new Vec2d(0, 0);
        Function<Optional<Integer>, Integer> markTailVisit = countOpt -> countOpt.orElse(0) + 1;
        visitedCount.putOrModify(tail, markTailVisit);

        for (String line : input) {
            Matcher matcher = MOVE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new Exception("Unhandled input");
            }
            Dir2d dir = Dir2d.fromChar(matcher.group(DIR).charAt(0));
            int moves = Integer.parseInt(matcher.group(MOVES));

            for (int i = 0; i < moves; ++i) {
                Pair<Vec2d, Vec2d> tailHeadUpdated = moveAndFollow(tail, head, dir);
                tail = tailHeadUpdated.getLeft();
                head = tailHeadUpdated.getRight();
                visitedCount.putOrModify(tail, markTailVisit);
            }
        }

        System.out.println(visitedCount.allElements().stream().mapToInt(v -> v).sum());
        System.out.println(visitedCount.numberOfElements());
    }

    private static Pair<Vec2d, Vec2d> moveAndFollow(Vec2d following, Vec2d followed, Dir2d dir) {
        Vec2d oldPos = new Vec2d(followed);
        followed = followed.inDir(dir);
        if (!following.touches(followed)) {
            Vec2d dest = new Vec2d(oldPos);
            oldPos = dest;
            following = dest;
        }
        return Pair.of(following, followed);
    }
}
