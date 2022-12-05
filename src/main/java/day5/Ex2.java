package day5;

import common.FileParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ex2 {
    private static final String COUNT = "count";
    private static final String FROM = "from";
    private static final String TO = "to";

    private static final String MOVE_REGEXP = String.format("move (?<%s>\\d+) from (?<%s>\\d+) to (?<%s>\\d+)", COUNT, FROM, TO);
    private static final Pattern MOVE_PATTERN = Pattern.compile(MOVE_REGEXP);

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input05.txt");

        int stacksCount = (input.get(0).length() + 1) / 4;

        List<Stack<Character>> stacks = new LinkedList<>();
        List<Stack<Character>> stacksReverse = new LinkedList<>();

        for (int i = 0; i < stacksCount; ++i) {
            stacks.add(new Stack<>());
            stacksReverse.add(new Stack<>());
        }

        int i = 0;
        for (String line = input.get(i); line.charAt(1) != '1'; line = input.get(++i)) {
            for (int j = 1; j < line.length(); j += 4){
                char box = line.charAt(j);
                if (box != ' ') {
                    stacksReverse.get((j - 1) / 4).push(box);
                }
            }
        }
        for (int stack_i = 0; stack_i < stacks.size(); ++stack_i) {
            Stack<Character> stackFrom = stacksReverse.get(stack_i);
            Stack<Character> stackTo = stacks.get(stack_i);
            while (!stackFrom.empty()) {
                stackTo.push(stackFrom.pop());
            }
        }
        i += 2;
        System.out.println("=====");
        for (; i < input.size(); i++) {
            String line = input.get(i);
            Matcher matcher = MOVE_PATTERN.matcher(line);
            matcher.matches();
            int count = Integer.parseInt(matcher.group(COUNT));
            int from = Integer.parseInt(matcher.group(FROM)) - 1;
            int to = Integer.parseInt(matcher.group(TO)) - 1;
            Stack<Character> stackFrom = stacks.get(from);
            Stack<Character> stackTo = stacks.get(to);
            Stack<Character> tmp = new Stack<>();
            for (int j = 0; j < count; ++j) {
                tmp.push(stackFrom.pop());
            }
            for (int j = 0; j < count; ++j) {
                stackTo.push(tmp.pop());
            }
        }

        for (i = 0; i < stacks.size(); ++i) {
            System.out.print(stacks.get(i).peek());
        }
    }
}
