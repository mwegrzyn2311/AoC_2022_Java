package day12;

import common.Dir2d;
import common.FileParser;
import common.Grid2d;
import common.Vec2d;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Ex2 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input12.txt");

        Mountain mountain = Mountain.fromStringList(input);

        //abcdefghijklmnopqrstuvwxyz

        List<Vec2d> dirs = mountain.findShortestRouteRecursive();

        System.out.println(dirs);
        System.out.println(dirs.size() - 1);
    }

    static class Mountain {
        Grid2d<Character> heights = new Grid2d<>();
        Vec2d start = null;
        Vec2d end = null;

        private boolean canMove(Vec2d from, Vec2d to) {
            return heights.eleAt(to).get() - heights.eleAt(from).get() <= 1;
        }

        private boolean canMoveReverse(Vec2d from, Vec2d to) {
            return heights.eleAt(to).get() - heights.eleAt(from).get() >= -1;
        }

        private List<Vec2d> allLowestPoints() {
            return heights.getGrid().entrySet().stream()
                    .filter(posToHeight -> posToHeight.getValue() == 'a')
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        private void shortestRouteFn(Vec2d lastPos, Vec2d curr, List<Vec2d> visited, List<Vec2d> shortest,
                                     Grid2d<Integer> bestScorePerPosition) {
            if (visited.contains(curr))
                return;
            if (!heights.hasEleAt(curr))
                return;
            if (lastPos != null && !canMove(lastPos, curr))
                return;

            Optional<Integer> bestScoreAtCurr = bestScorePerPosition.eleAt(curr);
            if (bestScoreAtCurr.isEmpty()) {
                bestScorePerPosition.add(curr, visited.size());
            } else {
                if (bestScoreAtCurr.get() <= visited.size())
                    return;
                else
                    bestScorePerPosition.add(curr, visited.size());
            }

            visited.add(curr);

            if (!shortest.isEmpty() && visited.size() >= shortest.size())
                return;

            if (end.equals(curr)) {
                System.out.printf("New shortest found with length of %d.%n", visited.size());
                shortest.clear();
                shortest.addAll(visited);
                return;
            }

            for (Dir2d dir : Dir2d.values()) {
                Vec2d newPos = curr.inDir(dir);
                shortestRouteFn(curr, newPos, new LinkedList<>(visited), shortest, bestScorePerPosition);
            }
        }

        public List<Vec2d> findShortestRouteRecursive() {
            List<Vec2d> res = new LinkedList<>();
            Grid2d<Integer> bestScoresAtPos = new Grid2d<>();
            for (Vec2d startingPoint : allLowestPoints()) {
                shortestRouteFn(null, startingPoint, new LinkedList<>(), res, bestScoresAtPos);
            }
            return res;
        }

        public static Mountain fromStringList(List<String> input) {
            Mountain mountain = new Mountain();
            for (int y = 0; y < input.size(); ++y) {
                String line = input.get(y);
                char[] chars = line.toCharArray();
                for (int x = 0; x < chars.length; ++x)
                    mountain.addEle(new Vec2d(x, y), chars[x]);
            }

            return mountain;
        }

        private Mountain() {
        }

        private void addEle(Vec2d pos, char c) {
            if (c == 'S') {
                start = pos;
                c = 'a';
            } else if (c == 'E') {
                end = pos;
                c = 'z';
            }

            heights.add(pos, c);
        }
    }
}
