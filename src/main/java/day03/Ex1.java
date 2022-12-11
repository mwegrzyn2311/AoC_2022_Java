package day03;

import common.FileParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Ex1 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input03.txt");

        List<RackSack> sacks = input.stream()
                .map(RackSack::fromString)
                .collect(Collectors.toList());

        int res = sacks.stream()
                .mapToInt(RackSack::findValOfCommonItem)
                .sum();

        System.out.println(res);
    }

    static class RackSack {
        private final Compartment c1;
        private final Compartment c2;

        public static RackSack fromString(String asStr) {
            return new RackSack(
                    Compartment.fromString(asStr.substring(0, asStr.length()/2)),
                    Compartment.fromString(asStr.substring(asStr.length()/2))
            );
        }

        public int findValOfCommonItem() {
            return c1.commonItem(c2).getVal();
        }

        public Item findCommonItem() {
            return c1.commonItem(c2);
        }

        public RackSack(Compartment c1, Compartment c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public String toString() {
            return c1.toString() + " | " + c2.toString();
        }
    }

    static class Compartment {
        private final List<Item> items;

        public Item commonItem(Compartment another) {
            List<Item> commonItems = new LinkedList<>(items);
            commonItems.retainAll(another.items);
            return commonItems.get(0);
        }

        public static Compartment fromString(String line) {
            return new Compartment(line.codePoints()
                    .mapToObj(unicode -> new Item((char) unicode))
                    .collect(Collectors.toList()));
        }

        public Compartment(List<Item> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return items.stream().map(Item::toString).collect(Collectors.joining());
        }
    }

    static class Item {
        private final char identifier;

        public int getVal() {
            return (Character.getNumericValue(identifier) - 9) + (Character.isLowerCase(identifier) ? 0 : 26);
        }

        public Item(char identifier) {
            this.identifier = identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return identifier == item.identifier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toString() {
            return String.valueOf(identifier);
        }
    }
}
