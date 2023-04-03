package algorithms.GeneticAlgorithm.company;

import java.io.IOException;

public class Runner {

    public static void main(String args[]) throws IOException {

        int counter = 1;

        float[] averages = new float[counter];
        float[] standardDev = new float[counter];
        int[] totalPerms = new int[counter];
        int[] totalExtraPerms = new int[counter];
        int[] foundSolutions = new int[counter];

        for (int i = 0; i < counter; i++) {
            MainQAPSolution mainQAPSolution1 = new MainQAPSolution();
            mainQAPSolution1.randomized = true;
            mainQAPSolution1.runHelperAlgorirthm = true;
            mainQAPSolution1.main();
            int totalPermutations = (int) mainQAPSolution1.count + mainQAPSolution1.extraCount;
            averages[i] = mainQAPSolution1.averageCost;
            standardDev[i] = (float) mainQAPSolution1.standardDeviation;
            totalPerms[i] = totalPermutations;
            totalExtraPerms[i] = mainQAPSolution1.extraCount;
            foundSolutions[i] = mainQAPSolution1.min;
        }

        float newAverage = 0;
        float newSD = 0;
        float newTotalPerm = 0;
        float newExtra = 0;
        float newAverageSolution = 0;
        float newSolutionSD = 0;

        for (int i = 0; i < counter; i++) {
            newAverage = newAverage + averages[i];
            newSD = newSD + standardDev[i];
            newTotalPerm = newTotalPerm + totalPerms[i];
            newExtra = newExtra + totalExtraPerms[i];
            newAverageSolution = newAverageSolution + foundSolutions[i];
        }

        for (int sol : foundSolutions) {
            newSolutionSD = newSolutionSD + ((sol - (newAverageSolution / counter)) * (sol - (newAverageSolution / counter)));
        }

        newSolutionSD = newSolutionSD / counter;
        newSolutionSD = (float) Math.sqrt(newSolutionSD);


        newAverage = newAverage / counter;
        newSD = newSD / counter;
        newExtra = newExtra / counter;
        newTotalPerm = newTotalPerm / counter;
        newAverageSolution = newAverageSolution / counter;

        System.out.println("New Average: " + newAverage);
        System.out.println("New SD: " + newSD);
        System.out.println("New ExtraCount: " + newExtra);
        System.out.println("New Total Permutations: " + newTotalPerm);
        String solutions = "";
        for (int num : foundSolutions) {
            solutions = solutions + num + ", ";
        }
        System.out.println("Solutions found: " + solutions);
        System.out.println("Solutions average: " + newAverageSolution);
        System.out.println("Solution standard deviation: " + newSolutionSD);

    }
}
