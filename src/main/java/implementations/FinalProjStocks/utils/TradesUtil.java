package implementations.FinalProjStocks.utils;

import java.util.ArrayList;
import java.util.Random;

public class TradesUtil {

    public static String predictTrade(String previousTrade) {
        double BB = 0.233333333;
        double BS = 0.416666667;
        double BH = 0.35;
        double SB = 0.318681319;
        double SS = 0.395604396;
        double SH = 0.285714286;
        double HB = 0.17;
        double HH = 0.53;
        double HS = 0.3;

        ArrayList<String> states = new ArrayList<>();
        states.add("B");
        states.add("S");
        states.add("H");
        double[][] probablities = new double[3][3];
        probablities[0] = new double[]{BB, BS, BH};
        probablities[1] = new double[]{SB, SS, SH};
        probablities[2] = new double[]{HB, HS, HH};


        Random random = new Random();
//        int ranIntStart = random.nextInt(3);
//        getNextLetter(ranIntStart);

        int ranIntStart = 0;

        if (previousTrade.equalsIgnoreCase("B"))
            ranIntStart = 0;
        if (previousTrade.equalsIgnoreCase("S"))
            ranIntStart = 1;
        if (previousTrade.equalsIgnoreCase("H"))
            ranIntStart = 2;


        double tempRan = random.nextDouble();
        double cumulative = 0.0;
        int counter = 0;
        boolean found = false;
        for (double d : probablities[ranIntStart]) {
            cumulative += d;
            if (tempRan < cumulative && !found) {
                ranIntStart = counter;
                found = true;
            }
            counter++;
        }
        System.out.print(" " + states.get(ranIntStart));
        return states.get(ranIntStart);
    }
}
