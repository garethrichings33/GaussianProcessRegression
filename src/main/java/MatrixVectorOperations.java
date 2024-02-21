package main.java;
public class MatrixVectorOperations {
    static public double[] squareMatrixVectorProduct(double matrix[][], double vector[]){
        if(!matrixIsSquare(matrix))
            throw new IllegalStateException("Matrix is not square.");
        if(!matrixAndVectorDimensionsMatch(matrix, vector))
            throw new IllegalStateException("Matrix and vector dimensions do not match.");

        double[] result = new double[vector.length];
        int length = vector.length;
        for(int i = 0; i < length; i++){
            result[i] = matrix[i][0] * vector[0];
            for(int j = 1; j < length; j++)
                result[i] += matrix[i][j] * vector[j];
        }
        return result;
    }

    static private boolean matrixAndVectorDimensionsMatch(double matrix[][], double vector[]){
        return matrix[0].length == vector.length;
    }
    static private boolean matrixIsSquare(double[][] matrix){
        return matrix.length == matrix[0].length;
    }
}
