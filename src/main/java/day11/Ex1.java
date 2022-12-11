package day11;

import common.FileParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Ex1 {
    private static final String ITEMS = "items";
    private static final String ITEMS_FORMAT = format("Starting items: (?<%s>[\\d ,]+)", ITEMS);
    private static final Pattern ITEMS_PATTERN = Pattern.compile(ITEMS_FORMAT);
    private static final String TO = "to";
    private static final String THROW_FORMAT = format("If (true|false): throw to monkey (?<%s>\\d+)", TO);
    private static final Pattern THROW_PATTERN = Pattern.compile(THROW_FORMAT);

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input11.txt");

        Circle circle = new Circle();

        for (int i = 0; i < input.size(); i += 7) {
            Matcher itemsMatcher = ITEMS_PATTERN.matcher(input.get(i + 1).trim());
            Matcher throwTrueMatcher = THROW_PATTERN.matcher(input.get(i + 4).trim());
            Matcher throwFalseMatcher = THROW_PATTERN.matcher(input.get(i + 5).trim());

            if (!itemsMatcher.matches() || !throwTrueMatcher.matches() || !throwFalseMatcher.matches()) {
                throw new Exception("Unhandled input");
            }

            circle.addMonkey(new Monkey(
                    itemsMatcher.group(ITEMS),
                    input.get(i + 2).trim(),
                    input.get(i + 3).trim(),
                    Integer.parseInt(throwTrueMatcher.group(TO)),
                    Integer.parseInt(throwFalseMatcher.group(TO))));
        }

        for (int i = 0; i < 20; ++i) {
            circle.round();
        }

        circle.printSummary();
    }

    static class Circle {
        List<Monkey> monkeys = new LinkedList<>();

        public void addMonkey(Monkey monkey) {
            this.monkeys.add(monkey);
        }

        public void round() {
            for (Monkey monkey : monkeys) {
                System.out.printf("Monkey %d:%n", monkeys.indexOf(monkey));
                monkey.turn(monkeys);
            }
        }

        public void printSummary() {
            for (Monkey monkey : monkeys) {
                System.out.printf("Monkey %d inspected items %d times.%n", monkeys.indexOf(monkey),
                        monkey.getInspectionsCount());
            }
        }
    }

    static class Monkey {
        private static final String OP = "op";
        private static final String VAL = "val";
        private static final String OPERATION_FORMAT = format("Operation: new = old (?<%s>[\\/*-+]) (?<%s>(\\d+|old))",
                OP, VAL);
        private static final Pattern OPERATION_PATTERN = Pattern.compile(OPERATION_FORMAT);
        private static final String THROW_TEST_FORMAT = format("Test: divisible by (?<%s>\\d+)", VAL);
        private static final Pattern THROW_TEST_PATTERN = Pattern.compile(THROW_TEST_FORMAT);


        private List<Item> items;
        private final Function<Integer, Integer> operation;
        private final Function<Integer, Boolean> throwTest;
        private final int throwToTrue;
        private final int throwToFalse;

        private int inspectionsCount = 0;

        public void turn(List<Monkey> monkeys) {
            for (Item item : items) {
                inspectItem(item, monkeys);
            }
            items.clear();
        }

        public void inspectItem(Item item, List<Monkey> monkeys) {
            System.out.printf("  Monkey inspects an item with a worry level of %d.%n", item.getWorryLevel());
            this.inspectionsCount++;
            item.getInspected(operation);
            item.getBored();
            if (item.throwTest(throwTest)) {
                System.out.println("    Current worry level is divisible");

                monkeys.get(throwToTrue).catchItem(item, throwToTrue);
            } else {
                System.out.println("    Current worry level is not divisible");
                monkeys.get(throwToFalse).catchItem(item, throwToFalse);
            }
        }

        public void catchItem(Item item, int idx) {
            System.out.printf("Item with worry level %d is thrown to monkey %d.%n", item.getWorryLevel(), idx);
            this.items.add(item);
        }

        public int getInspectionsCount() {
            return this.inspectionsCount;
        }

        public Monkey(String itemsStr, String operationStr, String throwTestStr, int throwToTrue, int throwToFalse)
                throws Exception {
            this.items = Item.listFromStr(itemsStr);
            this.operation = operationFromStr(operationStr);
            this.throwTest = throwTestFromStr(throwTestStr);
            this.throwToTrue = throwToTrue;
            this.throwToFalse = throwToFalse;
        }

        private static Function<Integer, Integer> operationFromStr(String operationStr) throws Exception {
            Matcher matcher = OPERATION_PATTERN.matcher(operationStr);
            if (!matcher.matches()) {
                throw new Exception(format("Unexpected input: %s", operationStr));
            }
            char op = matcher.group(OP).charAt(0);
            String val = matcher.group(VAL);

            if (val.equals("old")) {
                return switch(op) {
                    case '/' -> (old -> old / old);
                    case '*' -> (old -> old * old);
                    case '-' -> (old -> old - old);
                    case '+' -> (old -> old + old);
                    default -> throw new Exception(format("Unhandled op %c", op));
                };
            } else {
                int valNumeric = Integer.parseInt(val);
                return switch (op) {
                    case '/' -> (old -> old / valNumeric);
                    case '*' -> (old -> old * valNumeric);
                    case '-' -> (old -> old - valNumeric);
                    case '+' -> (old -> old + valNumeric);
                    default -> throw new Exception(format("Unhandled op %c", op));
                };
            }
        }

        private static Function<Integer, Boolean> throwTestFromStr(String throwTestStr) throws Exception {
            Matcher matcher = THROW_TEST_PATTERN.matcher(throwTestStr);
            if (!matcher.matches()) {
                throw new Exception(format("Unexpected input: %s", throwTestStr));
            }
            int divisibleBy = Integer.parseInt(matcher.group(VAL));

            return (val) -> val % divisibleBy == 0;
        }
    }

    static class Item {
        private static final int BOREDOM_DIVISION = 3;
        private int worryLevel;

        public static List<Item> listFromStr(String itemsStr) {
            return Arrays.stream(itemsStr.split(", "))
                    .map(Integer::parseInt)
                    .map(Item::new)
                    .collect(Collectors.toList());
        }

        public void getInspected(Function<Integer, Integer> operation) {
            int pre = this.worryLevel;
            this.worryLevel = operation.apply(worryLevel);
            System.out.printf("    Worry level %d -> %d.%n", pre, this.worryLevel);
        }

        public void getBored() {
            this.worryLevel /= BOREDOM_DIVISION;
            System.out.printf("    Monkey gets bored with item. Worry level is divided by %d to %d.", BOREDOM_DIVISION,
                    this.worryLevel);
        }

        public boolean throwTest(Function<Integer, Boolean> throwTestFn) {
            return throwTestFn.apply(this.worryLevel);
        }

        public int getWorryLevel() {
            return this.worryLevel;
        }

        public Item(int worryLevel) {
            this.worryLevel = worryLevel;
        }
    }
}
