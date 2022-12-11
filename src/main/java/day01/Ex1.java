package day01;

import common.FileParser;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Ex1 {

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

        int max = caloriesPerElf.stream()
                .map(elfCalories -> elfCalories.calories)
                .max(Comparator.comparingInt(a -> a))
                .orElse(-1);

        System.out.println(max);
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
