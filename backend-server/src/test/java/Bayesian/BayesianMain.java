package Bayesian;

import com.implementations.models.common.Vector;
import com.implementations.models.Bayesian.BayesianClassifier;

public class BayesianMain {

    public static void main(String[] args) {
        // Test simple scenario where x is always less than y in each vector and x && y > 0
        Vector[] vectors1 = new Vector[]{
                new Vector(2, 6),
                new Vector(3, 4),
                new Vector(3, 8),
                new Vector(4, 6)
        };
        BayesianClassifier class1 = new BayesianClassifier(vectors1);
        class1.inverseCovariance.printMatrix();
        System.out.println(class1);

        // Test simple scenario where x is always greater than y in each vector and x > 0,  y <= 0
        Vector[] vectors2 = new Vector[]{
                new Vector(1, -2),
                new Vector(3, 0),
                new Vector(3, -4),
                new Vector(5, -2)
        };
        BayesianClassifier class2 = new BayesianClassifier(vectors2);
        class2.inverseCovariance.printMatrix();
        System.out.println(class2);
    }

    // TODO: method to create pairs from file

    // TODO: method to read file
}
