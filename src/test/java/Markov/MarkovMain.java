package Markov;

import models.Markov.MarkovModel;

import java.util.ArrayList;
import java.util.Random;

public class MarkovMain {
    public static void main(String[] args) {
        String[] listOfWords = new String[]{"steam", "meet", "team", "mates", "seat", "tea", "same", "tame", "mess", "stats"};

        MarkovModel markovModel = new MarkovModel(listOfWords);
        markovModel.createLetterArray();

        markovModel.createGrid();

        for (int i = 10; i > 0; i--) {
            System.out.println();
            System.out.println(markovModel.createWord());
        }

//
//        0.3	0.53	0.17	0.395604396	0.285714286	0.318681319	0.233333333	0.35	0.416666667
//        30	53	17	36	26	29	14	21	25
//        H	H	H	S	S	S	B	B	B
//        100			91			60
//        HS	HH	HB	SS	SH	SB	BB	BH	BS


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
        double[][] probabilities = new double[3][3];
        probabilities[0] = new double[]{BB, BS, BH};
        probabilities[1] = new double[]{SB, SS, SH};
        probabilities[2] = new double[]{HB, HS, HH};


        Random random = new Random();
        int ranIntStart = random.nextInt(3);
        markovModel.getNextLetter(ranIntStart);

        for (int i = 50; i > 0; i--) {
            System.out.print(" " + states.get(ranIntStart));

            double tempRan = random.nextDouble();
            double cumulative = 0.0;
            int counter = 0;
            boolean found = false;
            for (double d : probabilities[ranIntStart]) {
                cumulative += d;
                if (tempRan < cumulative && !found) {
                    ranIntStart = counter;
                    found = true;
                }
                counter++;
            }
        }

    }

}
