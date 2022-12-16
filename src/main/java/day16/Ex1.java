package day16;

import common.FileParser;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class Ex1 {
    private static final int TIME_LIMIT = 30;
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

        System.out.println(getMaxFlow(pipesByLabels.get("AA"), null, new HashSet<>(), 0, 0));
    }

    private static long getMaxFlow(Pipe curr, Pipe prev, Set<Pipe> opened, int flow, int time) {
        if (time == TIME_LIMIT) {
            return flow;
        } else if (time > TIME_LIMIT) {
            return 0;
        }

        List<Pipe> possibleNext = new LinkedList<>(curr.neighbours);
        if (curr.neighbours.size() > 1 && nonNull(prev))
            possibleNext.remove(prev);

        return possibleNext.stream()
                .mapToLong(next -> opened.contains(next) || next.getFlowRate() == 0 ?
                        getMaxFlow(next, curr, new HashSet<>(opened), flow, time + 1) :
                        Math.max(
                                getMaxFlow(next, curr, new HashSet<>(opened), flow, time + 1),
                                openAndMaxFlow(next, curr, opened, flow, time)
                        )
                ).max().orElse(-1);
    }

    private static long openAndMaxFlow(Pipe next, Pipe curr, Set<Pipe> opened, int flow, int time) {
        Set<Pipe> newOpened = new HashSet<>(opened);
        newOpened.add(next);
        int timeAfterOpen = time + 2;
        int timeRemaining = TIME_LIMIT - timeAfterOpen;
        return getMaxFlow(next, curr, newOpened, flow + timeRemaining * next.getFlowRate(), timeAfterOpen);
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
