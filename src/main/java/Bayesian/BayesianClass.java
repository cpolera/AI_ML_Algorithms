package Bayesian;

import NearestNeighbor.Vector;

public class BayesianClass {


    Vector[] coords;
    Vector calculatedMean;
    Matrix covarianceMatrix = new Matrix(2, 2);
    Matrix inverseCovariance;

    public BayesianClass() {
    }

    public BayesianClass(Vector[] vectors) {
        coords = vectors;
        setCalculatedMean();
        calculateCovarienceMatrix();
        inverseCovariance = covarianceMatrix.inverseMatrix();
    }

    public void setCalculatedMean() {
        double x = 0;
        double y = 0;

        for (Vector coord : coords) {
            x = x + coord.x;
            y = y + coord.y;
        }

        calculatedMean = new Vector(x / coords.length, y / coords.length);
    }

    public void calculateCovarienceMatrix() {
        Matrix calcMeanMatrix = new Matrix(calculatedMean);
        Matrix tempSum = new Matrix(2, 2);
        for (int i = 0; i < coords.length; i++) {
            //ai - uj
            Matrix aSubi = new Matrix(coords[i]);

            Matrix lessMean = aSubi.subtractMatrix(calcMeanMatrix);

            //multiply ai - uj with its transpose
            Matrix lessMeanTranspose = lessMean.transposeMatrix();

            tempSum = tempSum.addMatrix(lessMean.multiplyMatrix(lessMeanTranspose));
        }
        System.out.println("The sum of ai - uj: ");
        tempSum.printMatrix();

        covarianceMatrix = tempSum.multiplyMatrix_Constant(1.0 / coords.length);
        System.out.println("The sum of ai - uj divided by count");
        covarianceMatrix.printMatrix();
    }

    public void printClass() {
        System.out.println("Coords: ");
        for (Vector coord : coords) {
            System.out.println(coord);
        }

        System.out.println("Calculated Mean: " + calculatedMean);
    }
}
