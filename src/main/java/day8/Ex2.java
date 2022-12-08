package day8;

import com.google.common.collect.ImmutableList;
import common.FileParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ex2 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input08.txt");

        List<List<Integer>> treeGrid = input.stream()
                .map(line -> line.chars()
                        .map(c -> c - 48)
                        .boxed()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        int count = IntStream.range(0, treeGrid.size())
                .map(row -> IntStream.range(0, treeGrid.get(row).size())
                        .map(col -> scenicScore(treeGrid, row, col))
                        .max()
                        .orElse(-1))
                .max()
                .orElse(-1);

        System.out.println(count);
    }

    private static int scenicScore(List<List<Integer>> treeGrid, int row, int col) {
        int height = treeGrid.get(row).get(col);

        int gridHeight = treeGrid.size();
        int gridWidth = treeGrid.get(0).size();

        List<Integer> treesInCol = treeGrid.stream()
                .mapToInt(treeRow -> treeRow.get(col))
                .boxed()
                .collect(Collectors.toList());

        List<Integer> toRight = treeGrid.get(row).subList(col + 1, gridWidth);
        List<Integer> toLeft = new ArrayList<>(treeGrid.get(row).subList(0, col));
        Collections.reverse(toLeft);
        List<Integer> toBottom = treesInCol.subList(row + 1, gridHeight);
        List<Integer> toTop = new ArrayList<>(treesInCol.subList(0, row));
        Collections.reverse(toTop);

        List<List<Integer>> treesInDirs = ImmutableList.of(toRight, toLeft, toBottom, toTop);

        return treesInDirs.stream()
                .map(dir -> viewingDistance(dir, height))
                .reduce(1, (total, dist) -> total * dist);
    }

    private static int viewingDistance(List<Integer> dir, int height) {
        int dist = 0;
        for (int tree : dir) {
            ++dist;
            if (tree >= height)
                break;
        }
        return dist;
    }
}
