package Feedfoward;

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
    
}
