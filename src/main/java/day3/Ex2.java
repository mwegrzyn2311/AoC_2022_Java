package day3;

import common.FileParser;

import java.util.*;
import java.util.stream.Collectors;

public class Ex2 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input03.txt");

        Groups groups = Groups.fromStringList(input);

        int res = groups.getBadgesValSum();

        System.out.println(res);
    }

    static class Groups {
        private final List<Group> groups;

        public static Groups fromStringList(List<String> lines) {
            List<Group> groups = new ArrayList<>(Collections.singletonList(new Group()));
            for (String line: lines) {
                if (groups.get(groups.size() - 1).isFull()) {
                    groups.add(new Group());
                }
                groups.get(groups.size() - 1).addElf(RackSack.fromString(line));
            }
            return new Groups(groups);
        }

        public int getBadgesValSum() {
            return groups.stream().mapToInt(group -> group.findBadge().getVal()).sum();
        }

        public Groups(List<Group> groups) {
            this.groups = groups;
        }
    }

    static class Group {
        private List<RackSack> elves = new LinkedList<>();

        public Item findBadge() {
            List<Item> commonItems = new LinkedList<>(elves.get(0).items);
            elves.subList(1, elves.size()).forEach(elf -> commonItems.retainAll(elf.items));
            return commonItems.get(0);
        }

        public void addElf(RackSack elf) {
            elves.add(elf);
        }

        public boolean isFull() {
            return this.elves.size() >= 3;
        }
    }

    static class RackSack {
        private final List<Item> items;

        public static RackSack fromString(String line) {
            return new RackSack(line.codePoints()
                    .mapToObj(unicode -> new Item((char) unicode))
                    .collect(Collectors.toList()));
        }

        public RackSack(List<Item> items) {
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
