package day04;

import common.FileParser;

import java.util.List;
import java.util.stream.Collectors;

public class Ex2 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input04.txt");

        List<RangePair> elfPairs = input.stream()
                .map(RangePair::fromString)
                .collect(Collectors.toList());

        long res = elfPairs.stream()
                .filter(RangePair::overlaps)
                .count();

        System.out.println(res);
    }


    static class RangePair {
        private final Range range1;
        private final Range range2;

        public boolean overlaps() {
            return range1.overlappedByOther(range2) || range2.overlappedByOther(range1);
        }

        public static RangePair fromString(String line) {
            String[] ranges = line.split(",");
            return new RangePair(
                    Range.fromString(ranges[0]),
                    Range.fromString(ranges[1]));
        }

        private RangePair(Range range1, Range range2) {
            this.range1 = range1;
            this.range2 = range2;
        }
    }

    static class Range {
        private final int left;
        private final int right;

        public boolean overlappedByOther(Range other) {
            return this.left >= other.left && this.left <= other.right;
        }

        public static Range fromString(String asStr) {
            String[] leftAndRight = asStr.split("-");
            return new Range(
                    Integer.parseInt(leftAndRight[0]),
                    Integer.parseInt(leftAndRight[1]));
        }

        private Range(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }
}
