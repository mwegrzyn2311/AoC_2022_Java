package day6;

import common.FileParser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Ex2 {
    private static final int LENGTH = 14;

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input06.txt");

        String signal = input.get(0);

        List<Character> header = new LinkedList<>();

        for (int i = 0; i < signal.length(); ++i) {
            if (header.size() == LENGTH) {
                header.remove(0);
            }
            header.add(signal.charAt(i));
            if (header.size() == LENGTH && allDistinct(header)) {
                System.out.println(i + 1);
                break;
            }
        }
    }

    private static boolean allDistinct (List<Character> header) {
        return new HashSet<Character>(header).size() == header.size();
    }
}
