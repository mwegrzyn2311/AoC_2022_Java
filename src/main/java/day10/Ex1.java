package day10;

import common.FileParser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Ex1 {
    private static final String NOOP = "noop";
    private static final String VAL = "val";
    private static final String ADDX_FORMAT = format("addx (?<%s>[-\\d]+)", VAL);
    private static final Pattern ADDX_PATTERN = Pattern.compile(ADDX_FORMAT);

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input10.txt");

        CPU cpu = new CPU();

        for (String line : input) {
            if (line.equals(NOOP))
                cpu.noop();
            else {
                Matcher addxMatcher = ADDX_PATTERN.matcher(line);
                if (addxMatcher.matches()) {
                    cpu.addx(Integer.parseInt(addxMatcher.group(VAL)));
                } else {
                    throw new Exception("Unhandled input");
                }
            }
        }

        System.out.printf("Reg = %d; Cycles = %d%n", cpu.getRegVal(), cpu.getCycles());
        System.out.printf("Res = %d%n", cpu.getStrenghtsSum());
    }

    static class CPU {
        private final Register reg = new Register();
        private int cycles = 1;
        private final List<Integer> signalStrengths = new LinkedList<>();

        private void incCycles(int cyclesInc) {
            this.cycles += cyclesInc;
            int strengthOrder = cycles <= 20 ? 0 : ((cycles - 21) / 40) + 1;
            if (strengthOrder > signalStrengths.size()) {
                logSignalStrength(strengthOrder);
            }
        }

        private void logSignalStrength(int strengthOrder) {
            signalStrengths.add(reg.getX() * (strengthOrder * 40 - 20));
        }

        public void addx(int val) {
            int cyclesInc = 2;
            incCycles(cyclesInc);
            this.reg.inc(val);
        }

        public void noop() {
            int cyclesInc = 1;
            incCycles(cyclesInc);
        }

        public int getStrenghtsSum() {
            incCycles(1);
            this.cycles -= 1;
            return signalStrengths.stream().mapToInt(v -> v).sum();
        }

        public int getCycles() {
            return this.cycles;
        }

        public int getRegVal() {
            return this.reg.getX();
        }
    }

    static class Register {
        private int x = 1;

        public void inc(int val) {
            this.x += val;
        }

        public int getX() {
            return this.x;
        }
    }
}
