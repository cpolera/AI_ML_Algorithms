package Bayesian;

import NearestNeighbor.Vector;

public class BMain {


    public static void main(String[] args) {

        Vector[] vectors1 = new Vector[]{new Vector(2, 6), new Vector(3, 4), new Vector(3, 8), new Vector(4, 6)};
        BayesianClass class1 = new BayesianClass(vectors1);
        class1.inverseCovariance.printMatrix();

        Vector[] vectors2 = new Vector[]{new Vector(1, -2), new Vector(3, 0), new Vector(3, -4), new Vector(5, -2)};
        BayesianClass class2 = new BayesianClass(vectors2);
        class2.inverseCovariance.printMatrix();

    }


    //method to create pairs from file

    //method to read file

}
