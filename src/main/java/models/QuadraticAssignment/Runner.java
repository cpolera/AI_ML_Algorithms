package models.QuadraticAssignment;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class Runner {

    public static void main(String args[]) throws IOException {
        Dotenv.configure().systemProperties().load();

        QAP QAP1 = new QAP();
        QAP1.file = "had122.txt";
        QAP1.runSolution();
    }
}
