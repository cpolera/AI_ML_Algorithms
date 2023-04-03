package Bayesian;

import common.Vector;

public class Matrix {

    double[][] matrix;
    double determinant;

    /**
     * Constructor to build a matrix based on given rows and columns.
     * All values are set to 0 to start with
     *
     * @param rows
     * @param columns
     */
    public Matrix(int rows, int columns) {
        matrix = new double[rows][columns];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /**
     * Constructor to generate a Matrix object from a given vector
     *
     * @param vector
     */
    public Matrix(Vector vector) {
        matrix = new double[2][1];
        matrix[0][0] = vector.x;
        matrix[1][0] = vector.y;
    }

    public Matrix transposeMatrix() {
        Matrix transposedMatrix = new Matrix(this.matrix[0].length, this.matrix.length);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposedMatrix.matrix[j][i] = matrix[i][j];
            }
        }

        return transposedMatrix;
    }

    public Matrix addMatrix(Matrix secondMatrix) {
        Matrix sum = new Matrix(matrix.length, matrix[0].length);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sum.matrix[i][j] = matrix[i][j] + secondMatrix.matrix[i][j];
            }
        }

        return sum;
    }

    public Matrix subtractMatrix(Matrix secondMatrix) {
        Matrix sum = new Matrix(matrix.length, matrix[0].length);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sum.matrix[i][j] = matrix[i][j] - secondMatrix.matrix[i][j];
            }
        }

        return sum;
    }


    public void printMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print((matrix[i][j] + 0.0) + " | ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    public Matrix multiplyByMatrix(Matrix secondMatrix) {
        Matrix product = new Matrix(matrix.length, secondMatrix.matrix[0].length);

        for (int i = 0; i < product.matrix.length; i++) {
            for (int j = 0; j < product.matrix[0].length; j++) {
                double tempVal = 0.0;

                //sum of [0][0 1 2] * [0 1 2][0]
                for (int t = 0; t < matrix[0].length; t++) {
                    tempVal = tempVal + (matrix[i][t] * secondMatrix.matrix[t][j]);
                }
                product.matrix[i][j] = tempVal;

            }
        }
        return product;
    }

    public Matrix multiplyByConstant(double constant) {
        Matrix tempMatrix = new Matrix(matrix.length, matrix[0].length);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                tempMatrix.matrix[i][j] = constant * matrix[i][j];
            }
        }
        return tempMatrix;
    }

    public Matrix inverseMatrix() {
        Matrix tempMat = new Matrix(matrix.length, matrix[0].length);
        if (matrix.length != matrix[0].length || matrix.length > 2) {
            throw new IllegalArgumentException("NOT CORRECT SIZE MATRIX");
        }

        determinant = ((matrix[0][0] * matrix[1][1]) + (matrix[0][1] * matrix[1][0]));
        double tempDeterminant = 1.0 / determinant;


        tempMat.matrix[0][0] = matrix[1][1];
        tempMat.matrix[0][1] = matrix[1][0] * -1;
        tempMat.matrix[1][0] = matrix[0][1] * -1;
        tempMat.matrix[1][1] = matrix[0][0];

        System.out.println("Determinant: " + determinant);

        System.out.println("Inverse pre determinant");
        tempMat.printMatrix();

        return tempMat.multiplyByConstant(tempDeterminant);
    }
}
