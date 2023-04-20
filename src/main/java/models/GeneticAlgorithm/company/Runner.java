package models.GeneticAlgorithm.company;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

/**
 * Apr 20, 2023: This code is over 5 years old and needs to be heavily refactored. Lots of novice mistakes and issues
 */
public class Runner {

    public static void main(String args[]) throws IOException {
        Dotenv.configure().systemProperties().load();

        MainQAPSolution mainQAPSolution1 = new MainQAPSolution();
        mainQAPSolution1.file = "had122.txt";
        mainQAPSolution1.main();
    }
}
