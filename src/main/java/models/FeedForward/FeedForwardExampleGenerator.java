package models.FeedForward;

import models.FeedForward.components.NNObj;

public class FeedForwardExampleGenerator {
    public static Symbol[] createTrainingSymbolList() {

        int[] plusIntoVals = new int[]{
                0, 0, 1, 0, 0,
                0, 0, 1, 0, 0,
                1, 1, 1, 1, 1,
                0, 0, 1, 0, 0,
                0, 0, 1, 0, 0};

        int[] xIntoVals = new int[]{
                1, 0, 0, 0, 1,
                0, 1, 0, 1, 0,
                0, 0, 1, 0, 0,
                0, 1, 0, 1, 0,
                1, 0, 0, 0, 1};

        int[] oIntoVals = new int[]{
                1, 1, 1, 1, 1,
                1, 0, 0, 0, 1,
                1, 0, 0, 0, 1,
                1, 0, 0, 0, 1,
                1, 1, 1, 1, 1
        };

        return new Symbol[]{
                new Symbol(plusIntoVals, new int[]{0, 0, 1}, "PLUS"),
                new Symbol(xIntoVals, new int[]{0, 1, 0}, "X"),
                new Symbol(oIntoVals, new int[]{1, 0, 0}, "O")
        };
    }

    public static Symbol[] createTestSymbolList() {

        int[] plusSymbol = new int[]{
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0,
                1, 0, 1, 1, 1,
                0, 0, 1, 0, 0,
                0, 0, 1, 0, 0};

        int[] xIntoVals = new int[]{
                1, 1, 0, 1, 1,
                0, 1, 0, 1, 0,
                0, 0, 1, 0, 0,
                0, 1, 0, 1, 0,
                1, 0, 0, 0, 1};

        int[] OintoVals = new int[]{
                1, 1, 1, 1, 1,
                1, 0, 0, 1, 1,
                1, 0, 0, 0, 1,
                1, 0, 0, 0, 0,
                1, 1, 1, 1, 1
        };
        return new Symbol[]{
                new Symbol(plusSymbol, new int[]{0, 0, 1}, "PLUS_t"),
                new Symbol(xIntoVals, new int[]{0, 1, 0}, "X_t"),
                new Symbol(OintoVals, new int[]{1, 0, 0}, "O_t")
        };
    }
}
