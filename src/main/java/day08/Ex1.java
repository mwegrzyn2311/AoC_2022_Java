package day08;

import com.google.common.collect.ImmutableList;
import common.FileParser;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ex1 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input08.txt");

        List<List<Integer>> treeGrid = input.stream()
                .map(line -> line.chars()
                        .map(c -> c - 48)
                        .boxed()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        int count = IntStream.range(0, treeGrid.size())
                .reduce(0, (sum, row) -> sum += IntStream.range(0, treeGrid.get(row).size())
                        .filter(col -> isVisible(treeGrid, row, col))
                        .count());

        System.out.println(count);
    }

    private static boolean isVisible(List<List<Integer>> treeGrid, int row, int col) {
        int height = treeGrid.get(row).get(col);

        Supplier<IntStream> treesInRow = () -> treeGrid.get(row).stream().mapToInt(v -> v);
        Supplier<IntStream> treesInCol = () -> treeGrid.stream().mapToInt(treeRow -> treeRow.get(col));

        List<IntStream> treesInDirs = ImmutableList.of(
                // To the right
                treesInRow.get().skip(col + 1),
                // To the left
                treesInRow.get().limit(col),
                // To the bottom
                treesInCol.get().skip(row + 1),
                // To the top
                treesInCol.get().limit(row)
        );

        return treesInDirs.stream().anyMatch(dir -> visibleInDir(dir, height));
    }

    private static boolean visibleInDir(IntStream dir, int height) {
        return dir.noneMatch(tree -> tree >= height);
    }
}
