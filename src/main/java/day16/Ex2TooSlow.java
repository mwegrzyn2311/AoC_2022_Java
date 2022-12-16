package day16;

import common.FileParser;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class Ex2TooSlow {
    private static final int TIME_LIMIT = 26;
    private static final String VALVE_LABEL = "valveLabel";
    private static final String FLOW_RATE = "flowRate";
    private static final String NEIGH_VALVES = "neighValves";
    private static final Pattern PIPE_PATTERN = Pattern.compile(
            format("Valve (?<%s>.+) has flow rate=(?<%s>\\d+); (tunnels lead to valves|tunnel leads to valve) (?<%s>.+)",
                    VALVE_LABEL, FLOW_RATE, NEIGH_VALVES));

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input16.txt");

        Map<String, Pipe> pipesByLabels = new HashMap<>();

        for (String line : input) {
            if (!PIPE_PATTERN.matcher(line).matches()) {
                System.out.println(line);
            }
        }

        Supplier<Stream<Matcher>> inputMatchers = () -> input.stream()
                .map(PIPE_PATTERN::matcher)
                .filter(Matcher::matches);

        inputMatchers.get()
                .forEach(matcher -> pipesByLabels.put(
                        matcher.group(VALVE_LABEL), new Pipe(Integer.parseInt(matcher.group(FLOW_RATE)))));

        inputMatchers.get()
                .forEach(matcher -> Arrays.stream(matcher.group(NEIGH_VALVES).split(", "))
                        .forEach(neighLabel -> pipesByLabels.get(
                                matcher.group(VALVE_LABEL)).addNeighbour(pipesByLabels.get(neighLabel))));

        System.out.println(getMaxFlow(pipesByLabels.get("AA"), null, null, pipesByLabels.get("AA"), null, null, new HashSet<>(), 0, 0));
    }

    private static long getMaxFlow(Pipe currYou, Pipe prevYou, Action actionYou, Pipe currEleph, Pipe prevEleph,
                                   Action actionEleph, Set<Pipe> opened, long flow, int time) {
        if (actionYou == Action.OPEN)
            flow = openPipe(currYou, opened, flow, time);
        if (actionEleph == Action.OPEN)
            flow = openPipe(currEleph, opened, flow, time);

        if (time == TIME_LIMIT)
            return flow;

        List<NextIter> possibleNextEleph = possibleNext(currEleph, prevEleph, actionYou, opened);
        List<Pair<NextIter, NextIter>> possibleNextBoth = possibleNext(currYou, prevYou, actionEleph, opened).stream()
                .map(nextYou -> possibleNextEleph.stream()
                        .map(nextEleph -> Pair.of(nextYou, nextEleph))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .filter(nextPair -> !(currYou == currEleph && nextPair.getLeft().equals(nextPair.getRight())))
                .filter(Ex2TooSlow::notOpeningSamePipe)
                .collect(Collectors.toList());

        long finalFlow = flow;
        return possibleNextBoth.stream()
                .mapToLong(nextYouEleph -> getMaxFlow(nextYouEleph.getLeft().next, currYou, nextYouEleph.getLeft().action,
                        nextYouEleph.getRight().next, currEleph, nextYouEleph.getRight().action, new HashSet<>(opened),
                        finalFlow, time + 1))
                .max().orElse(-1);
    }



    private static long maxPossibleUpgrade(int time) {
        final long initRate = 25;
        return LongStream.range(0, (long) Math.ceil((TIME_LIMIT - time) / 2.0))
                .map(timeSubtract -> (initRate - timeSubtract) * 2 *(time - timeSubtract * 2))
                .sum();
    }

    private static boolean notOpeningSamePipe(Pair<NextIter, NextIter> nextYouEleph) {
        return !openingSamePipe(nextYouEleph);
    }

    private static boolean openingSamePipe(Pair<NextIter, NextIter> nextYouEleph) {
        return nextYouEleph.getLeft().action == Action.OPEN && nextYouEleph.getRight().action == Action.OPEN &&
                nextYouEleph.getLeft().next == nextYouEleph.getRight().next;
    }

    private static List<NextIter> possibleNext(Pipe curr, Pipe prev, Action currAction, Set<Pipe> opened) {
        List<NextIter> res = new LinkedList<>();
        if (curr.neighbours.size() > 1 && nonNull(prev)) {
            res.addAll(curr.getNeighbours().stream()
                    .map(neigh -> new NextIter(neigh, Action.MOVE))
                    .collect(Collectors.toList()));
        } else {
            res.addAll(curr.getNeighbours().stream()
                    .filter(neigh -> neigh != prev)
                    .map(neigh -> new NextIter(neigh, Action.MOVE))
                    .collect(Collectors.toList()));
        }
        if (currAction == Action.MOVE && !opened.contains(curr) && !(curr.getFlowRate() == 0)) {
            res.add(new NextIter(curr, Action.OPEN));
        }
            res.remove(prev);
        return res;
    }

    private static class NextIter {
        public final Pipe next;
        public final Action action;

        public NextIter(Pipe next, Action action) {
            this.next = next;
            this.action = action;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NextIter nextIter = (NextIter) o;
            return Objects.equals(next, nextIter.next) &&
                    action == nextIter.action;
        }

        @Override
        public int hashCode() {
            return Objects.hash(next, action);
        }
    }

    private static long openPipe(Pipe curr, Set<Pipe> opened, long flow, int time) {
        opened.add(curr);
        return flow + time * curr.getFlowRate();
    }

    private enum Action {
        MOVE,
        OPEN
    }

    static class Pipe {
        private List<Pipe> neighbours = new LinkedList<>();
        private final int flowRate;

        public Pipe(int flowRate) {
            this.flowRate = flowRate;
        }

        public void addNeighbour(Pipe neighbour) {
            this.neighbours.add(neighbour);
        }

        public int getFlowRate() {
            return flowRate;
        }

        public List<Pipe> getNeighbours() {
            return neighbours;
        }
    }
}
