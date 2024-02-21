package main.java;
public class Cholesky {
    static public void cholesky(double matrix[][]){
        int size = matrix.length;
        if(size != matrix[0].length)
            throw new IllegalArgumentException("Matrix is not square.");

        if(!isSymmetric(matrix))
            throw new IllegalArgumentException("Matrix is not symmetric.");

        double sum;
        for(int i =0; i<size; i++){
            for(int j=i; j<size;j++){
                sum = matrix[i][j];
                for(int k = i-1; k>=0; k--)
                    sum -= matrix[i][k]* matrix[j][k];
                if(i==j){
                    if(sum <= 0)
                        throw new IllegalStateException("Matrix is not positive-definite");
                    matrix[i][i] = Math.sqrt(sum);
                }
                else
                    matrix[j][i] = sum / matrix[i][i];
            }
        }
    }

    private static boolean isSymmetric(double[][] matrix) {
        for(int i = 0; i < matrix.length; i++)
            for(int j = i+1; j < matrix.length; j++)
                if(matrix[i][j] != matrix[j][i])
                    return false;

        return true;
    }

    static public double[] solveCholesky(double[][] matrix, double[] input){
        int size = matrix.length;
        if(size != matrix.length || size != matrix[0].length)
            throw new IllegalStateException("Matrix and vector of different dimensions.");

        double sum;
        double[] output = new double[size];
        for(int i = 0; i < size; i++){
            sum = input[i];
            for(int k = i-1; k >= 0; k--)
                sum -= matrix[i][k] * output[k];
            output[i] = sum / matrix[i][i];
        }
        for(int i = size-1; i>=0; i--){
            sum = output[i];
            for(int k = i+1; k < size; k++)
                sum -= matrix[k][i] * output[k];
            output[i] = sum / matrix[i][i];
        }
        return output;
    }
}