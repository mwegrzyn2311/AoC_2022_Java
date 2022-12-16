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

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Ex2Optimized {
    private static final int TIME_LIMIT = 26;
    private static final String VALVE_LABEL = "valveLabel";
    private static final String FLOW_RATE = "flowRate";
    private static final String NEIGH_VALVES = "neighValves";
    private static final Pattern PIPE_PATTERN = Pattern.compile(
            format("Valve (?<%s>.+) has flow rate=(?<%s>\\d+); (tunnels lead to valves|tunnel leads to valve) (?<%s>.+)",
                    VALVE_LABEL, FLOW_RATE, NEIGH_VALVES));

    private static final List<PipeShortestDist> NO_MOVES_LEFT = singletonList(new PipeShortestDist(null, 0));

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
                        matcher.group(VALVE_LABEL), new Pipe(matcher.group(VALVE_LABEL), Integer.parseInt(matcher.group(FLOW_RATE)))));

        inputMatchers.get()
                .forEach(matcher -> Arrays.stream(matcher.group(NEIGH_VALVES).split(", "))
                        .forEach(neighLabel -> pipesByLabels.get(
                                matcher.group(VALVE_LABEL)).addNeighbour(pipesByLabels.get(neighLabel))));

        Map<Pipe, List<PipeShortestDist>> shortestDists = pipesByLabels.values().stream()
                .collect(Collectors.<Pipe, Pipe, List<PipeShortestDist>>toMap(pipe -> pipe, pipe -> newArrayList(new PipeShortestDist(pipe, 0))));

        for (Map.Entry<Pipe, List<PipeShortestDist>> entry : shortestDists.entrySet()) {
            List<PipeShortestDist> list = entry.getValue();
            List<Pipe> lastlyAdded = newArrayList(entry.getKey());
            int dist = 1;
            while (list.size() != pipesByLabels.values().size()) {
                lastlyAdded = lastlyAdded.stream()
                        .map(Pipe::getNeighbours)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(neigh -> !list.stream().map(psd -> psd.to).collect(Collectors.toList()).contains(neigh))
                        .collect(Collectors.toList());

                int finalDist = ++dist;
                list.addAll(lastlyAdded.stream()
                        .map(neigh -> new PipeShortestDist(neigh, finalDist))
                        .collect(Collectors.toList()));
            }
        }

        // Leave only significant
        shortestDists.values().forEach(list -> list.removeAll(list.stream()
                .filter(psd -> psd.dist == 1 || psd.to.getFlowRate() == 0)
                .collect(Collectors.toList())));

        long allPipes = pipesByLabels.values().stream()
                .filter(pipe -> pipe.getFlowRate() != 0)
                .count();

        System.out.println(getMaxFlow(pipesByLabels.get("AA"), pipesByLabels.get("AA"), new HashSet<>(), 0, 0, 0, shortestDists, allPipes));
    }

    private static class PipeShortestDist {
        public final Pipe to;
        public final int dist;

        public PipeShortestDist(Pipe to, int dist) {
            this.to = to;
            this.dist = dist;
        }

        @Override
        public String toString() {
            return isNull(to) ? "X" : format("%s(%d)", to.toString(), dist);
        }
    }

    private static long getMaxFlow(Pipe currYou, Pipe currEleph, Set<Pipe> opened, long flow, int timeYou, int timeEleph, Map<Pipe, List<PipeShortestDist>> shortestDists, long allPipes) {
        //System.out.printf("timeYou=%d, timeEleph=%d, flow=%d.%n", timeYou, timeEleph, flow);

        if (opened.size() == allPipes || (isNull(currYou) || timeYou == TIME_LIMIT) && (isNull(currYou) || timeEleph == TIME_LIMIT))
            return flow;

        List<PipeShortestDist> possibleNextEleph = possibleNext(currEleph, opened, shortestDists, timeEleph);
        List<PipeShortestDist> possibleNextYou = possibleNext(currYou, opened, shortestDists, timeYou);
        return possibleNextEleph.stream()
                .mapToLong(nextEleph -> possibleNextYou.stream()
                        .filter(nextYou -> isNull(nextYou) || isNull(nextEleph) || nextYou.to != nextEleph.to)
                        //.peek(nextYou -> System.out.printf("%s, %s. %n", nextYou.toString(), nextEleph.toString()))
                        .mapToLong(nextYou -> getMaxFlow(nextYou.to, nextEleph.to, open(opened, nextYou.to, nextEleph.to),
                                flow + flowInc(nextYou, timeYou) + flowInc(nextEleph, timeEleph),
                                timeYou + nextYou.dist, timeEleph + nextEleph.dist, shortestDists, allPipes))
                        .max().orElse(flow))
                .max().orElse(flow);
    }

    private static long flowInc(PipeShortestDist next, int time) {
        return isNull(next.to) ? 0 : (TIME_LIMIT - time - next.dist) * next.to.getFlowRate();
    }

    private static Set<Pipe> open(Set<Pipe> opened, Pipe pipe1, Pipe pipe2) {
        Set<Pipe> res = new HashSet<>(opened);
        if (nonNull(pipe1))
            res.add(pipe1);
        if (nonNull(pipe2))
            res.add(pipe2);
        return res;
    }

    private static List<PipeShortestDist> possibleNext(Pipe curr, Set<Pipe> opened, Map<Pipe, List<PipeShortestDist>> shortestDists, int time) {
        if (isNull(curr))
            return NO_MOVES_LEFT;
        int timeRemaining = TIME_LIMIT - time;
        List<PipeShortestDist> res = shortestDists.get(curr).stream()
                .filter(psd -> !opened.contains(psd.to))
                .filter(psd -> psd.dist <= timeRemaining)
                .collect(Collectors.toList());
        return res.size() != 0 ? res : NO_MOVES_LEFT;
    }

    static class Pipe {
        private final String label;
        private List<Pipe> neighbours = new LinkedList<>();
        private final int flowRate;

        public Pipe(String label, int flowRate) {
            this.label = label;
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

        @Override
        public String toString() {
            return label;
        }
    }
}
