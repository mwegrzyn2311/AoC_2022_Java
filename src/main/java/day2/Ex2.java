package day2;

import common.FileParser;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Ex2 {

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input02.txt");

        List<RSPRound> gameRounds = input.stream()
                .map(RSPRound::fromString)
                .collect(Collectors.toList());

        int finalScore = gameRounds.stream()
                .mapToInt(RSPRound::getPlayer2Score)
                .sum();

        System.out.println(finalScore);
    }

    static class RSPRound {
        private final RSPPlayer player1;
        private final RSPPlayer player2;

        public static RSPRound fromString(String rspString) {
            RSPPlayer player1 = RSPPlayer.fromChar(rspString.charAt(0));
            RSPResult player2result = RSPResult.fromChar(rspString.charAt(2));
            RSPPlayer player2 = RSPPlayer.fromResultAndOpponent(player2result, player1);
            return new RSPRound(player1, player2);
        }

        public RSPRound(RSPPlayer player1, RSPPlayer player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        public int getPlayer1Score() {
            return getChoiceScore(player1) + getResultScore(player1, player2);
        }

        // Player2 is us for this input
        public int getPlayer2Score() {
            return getChoiceScore(player2) + getResultScore(player2, player1);
        }

        private int getChoiceScore(RSPPlayer player) {
            return player.getChoiceVal();
        }

        private int getResultScore(RSPPlayer calcFor, RSPPlayer opponent) {
            return RSPResult.getFromPlayers(calcFor, opponent).resultVal;
        }

        @Override
        public String toString() {
            return format("%s vs %s", player1.toString(), player2.toString());
        }
    }

     enum RSPResult {
        LOSS(0),
        DRAW(3),
        WIN(6);

        private final int resultVal;

        public static RSPResult fromChar(char resultChar) {
            return switch(resultChar) {
                case 'X' -> LOSS;
                case 'Y' -> DRAW;
                case 'Z' -> WIN;
                default -> throw new IllegalArgumentException(format("Input for RSPRound has to be one of {X,Y,Z} but was %c", resultChar));
            };
        }

         public static RSPResult getFromPlayers(RSPPlayer resultFor, RSPPlayer opponent) {
             int choicesDiff = resultFor.choiceVal - opponent.choiceVal;
             return switch (choicesDiff) {
                 case 0 -> DRAW;
                 case 1, -2 -> WIN;
                 case -1, 2 -> LOSS;
                 default -> throw new IllegalArgumentException(format("Unhandled choice diff %d from  [%s vs %s]", choicesDiff, resultFor.toString(), opponent.toString()));
             };
         }

        RSPResult(int resultVal) {
            this.resultVal = resultVal;
        }

        public int getResultVal() {
            return this.resultVal;
        }
    }

    enum RSPPlayer {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        private final int choiceVal;

        public static RSPPlayer fromChar(char choice) {
            return switch (choice) {
                case 'A' -> ROCK;
                case 'B' -> PAPER;
                case 'C' -> SCISSORS;
                default -> throw new IllegalArgumentException(format("Input for RSPPlayer has to be one of {A,B,C} but was %c", choice));
            };
        }

        public static RSPPlayer fromResultAndOpponent(RSPResult result, RSPPlayer opponent) {
            return switch (result) {
                case LOSS -> fromInt((opponent.choiceVal + 2) % 3);
                case DRAW -> opponent;
                case WIN -> fromInt((opponent.choiceVal + 1) % 3);
            };
        }

        public static RSPPlayer fromInt(int val) {
            return switch(val) {
                case 1 -> ROCK;
                case 2 -> PAPER;
                case 0, 3 -> SCISSORS;
                default -> throw new IllegalArgumentException(format("Val for RSPPlayer has to be one of {1,2,3} but was %d", val));
            };
        }

        RSPPlayer(int choiceVal) {
            this.choiceVal = choiceVal;
        }

        public int getChoiceVal() {
            return this.choiceVal;
        }

        @Override
        public String toString() {
            return this.name();
        }
    }
}
