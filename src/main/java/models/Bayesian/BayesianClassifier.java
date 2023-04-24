package models.Bayesian;

import common.Vector;

public class BayesianClassifier {

    Vector[] coords;
    Vector calculatedMean;
    Matrix covarianceMatrix = new Matrix(2, 2);
    public Matrix inverseCovariance;

    public BayesianClassifier() {}

    public BayesianClassifier(Vector[] vectors) {
        coords = vectors;
        setCalculatedMean();
        calculateCovarianceMatrix();
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

    public void calculateCovarianceMatrix() {
        Matrix calcMeanMatrix = new Matrix(calculatedMean);
        Matrix tempSum = new Matrix(2, 2);
        for (Vector coord : coords) {
            //ai - uj
            Matrix aSubI = new Matrix(coord);

            Matrix lessMean = aSubI.subtractMatrix(calcMeanMatrix);

            //multiply a sub i - uj with its transpose
            Matrix lessMeanTranspose = lessMean.transposeMatrix();

            tempSum = tempSum.addMatrix(lessMean.multiplyByMatrix(lessMeanTranspose));
        }
        System.out.println("The sum of ai - uj: ");
        tempSum.printMatrix();

        covarianceMatrix = tempSum.multiplyByConstant(1.0 / coords.length);
        System.out.println("The sum of ai - uj divided by count");
        covarianceMatrix.printMatrix();
    }

    public String toString() {
        String retString = "Coords: " + System.lineSeparator();
        for (Vector coord : coords) {
            retString += coord + System.lineSeparator();
        }

        retString += "Calculated Mean: " + calculatedMean + System.lineSeparator();
        return retString;
    }
}
