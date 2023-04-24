package common.implementations.models.FeedForward;

import java.util.ArrayList;

public class NNMath {
    private static double lambda = 1;
    private static double L = 1; //curve max val

    public static double sigmoidFunc(double d) {
        return L / (1 + Math.exp(-1 * lambda * d));
    }

    public static double calcRMSE(double TSSE, int patternCount, int outputPerPattern) {
        return Math.sqrt((2 * TSSE) / (patternCount * outputPerPattern));
    }

    public static double calcSimpleLineVal(double min, double max, double xVal) {
        double finalVar = (xVal - min) / (max - min);

        return finalVar;
    }

    public static double reverseSimpleLine(double min, double max, double finalVar) {
        double xVal = (finalVar) * (max - min) + min;

        return xVal;
    }

    public static double calculateSumOfSquares(ArrayList<Double[]> patternsE, ArrayList<Double[]> outActuals) {
        double sum = 0.0;
        for (int i = 0; i < outActuals.size(); i++) {
            sum = sum + calculateSquaredError_OnePattern(patternsE.get(i), outActuals.get(i));
        }

        return sum;
    }

    public static double calculateSquaredError_OnePattern(Double[] patExpect, Double[] outActual) {
        double sum = 0.0;
        for (int i = 0; i < patExpect.length; i++) {
            double temp = patExpect[i] - outActual[i];
            temp = temp * temp;
            sum = sum + temp;
        }
        return sum;
    }

    public static double calculateTSSE(ArrayList<Double[]> patternsE, ArrayList<Double[]> outActuals) {
        return 0.5 * calculateSumOfSquares(patternsE, outActuals);
    }
}
