public class MatrixVectorOperations {
    static public double[] matrixVectorProduct(double matrix[][], double vector[]){
        if(vector.length != matrix.length && matrix.length != matrix[0].length)
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
}
