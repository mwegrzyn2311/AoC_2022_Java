package day01;

import common.FileParser;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Ex2 {

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input01.txt");

        List<ElfCalories> caloriesPerElf = new LinkedList<>();
        ElfCalories curr = new ElfCalories();
        for (String line : input) {
            if (StringUtils.isBlank(line)) {
                caloriesPerElf.add(curr);
                curr = new ElfCalories();
            } else {
                curr.addCalories(Integer.parseInt(line));
            }
        }

        List<Integer> sortedCaloriesReversed = caloriesPerElf.stream()
                .map(elfCalories -> elfCalories.calories)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        int top3sum = sortedCaloriesReversed.get(0) + sortedCaloriesReversed.get(1) + sortedCaloriesReversed.get(2);
        System.out.println(top3sum);
    }

    static class ElfCalories {
        public int calories = 0;

        public void addCalories(int val) {
            this.calories += val;
        }

        @Override
        public String toString() {
            return String.valueOf(this.calories);
        }
    }
}
