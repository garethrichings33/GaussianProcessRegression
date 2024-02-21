package main.java;
public class MatrixVectorOperations {
    static public double[] squareMatrixVectorProduct(double matrix[][], double vector[]){
        if(!matrixAndVectorDimentionsMatch(matrix, vector))
            throw new IllegalStateException("Matrix and vector dimensions do not match.");
        if(!matrixIsSquare(matrix))
            throw new IllegalStateException("Matrix is not square.");

        double[] result = new double[vector.length];
        int length = vector.length;
        for(int i = 0; i < length; i++){
            result[i] = matrix[i][0] * vector[0];
            for(int j = 1; j < length; j++)
                result[i] += matrix[i][j] * vector[j];
        }
        return result;
    }

    static boolean matrixAndVectorDimentionsMatch(double matrix[][], double vector[]){
        return matrix.length == matrix[0].length;
    }
    static boolean matrixIsSquare(double[][] matrix){
        return matrix.length == matrix[0].length;
    }
}
