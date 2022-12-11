package day10;

import common.FileParser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Ex2 {
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

        cpu.draw();
    }

    static class CPU {
        private final Register reg = new Register();
        private int cycles = 1;
        private final List<Integer> signalStrengths = new LinkedList<>();
        private final List<Character> image = new LinkedList<>();

        private void incCycles(int cyclesInc) {
            for (int i = 0; i < cyclesInc; ++i) {
                incCycleAndWriteSymbol();
            }
            int strengthOrder = cycles <= 20 ? 0 : ((cycles - 21) / 40) + 1;
            if (strengthOrder > signalStrengths.size()) {
                logSignalStrength(strengthOrder);
            }
        }

        private void incCycleAndWriteSymbol() {
            ++this.cycles;
            image.add(shouldDraw() ? '#' : '.');
        }

        private boolean shouldDraw() {
            int currX = image.size() % 40;
            return currX >= reg.getX() - 1 && currX <= reg.getX() + 1;
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

        public void draw() {
            for(int i = 0; i < image.size(); ++i) {
                System.out.print(image.get(i));
                if (i%40 == 39)
                    System.out.println();
            }
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
