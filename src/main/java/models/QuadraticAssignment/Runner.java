package models.QuadraticAssignment;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class Runner {

    public static void main(String args[]) throws IOException {
        Dotenv.configure().systemProperties().load();

        QAP QAP1 = new QAP("qap4.txt"); // 4! = 24
//        QAP QAP1 = new QAP("qap20.txt"); // 20! = 2,432,902,008,176,640,000
        QAP1.runSolution();
    }
}
