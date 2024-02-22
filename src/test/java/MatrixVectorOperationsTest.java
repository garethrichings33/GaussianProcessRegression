import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatrixVectorOperationsTest {
    final double delta = 1.0E-12;

    @Nested
    class squareMatrixVectorProductTests {
        @Test
        @DisplayName("Should throw an exception if matrix and vector dimensions do not match")
        void squareMatrixVectorProductDimensionsDoNotMatch() {
            double[][] matrix = {{1, 2}, {1, 2}};
            double[] vector = {1, 2, 3};

            assertThrows(IllegalStateException.class,
                    () -> {
                        MatrixVectorOperations.squareMatrixVectorProduct(matrix, vector);
                    });
        }

        @Test
        @DisplayName("Should throw an exception if matrix is not square")
        void squareMatrixVectorProductMatrixNotSquare() {
            double[][] matrix = {{1, 2, 3}, {1, 2, 3}};
            double[] vector = {1, 2, 3};

            assertThrows(IllegalStateException.class,
                    () -> {
                        MatrixVectorOperations.squareMatrixVectorProduct(matrix, vector);
                    });
        }

        @Test
        @DisplayName("Should return zero vector if using zero matrix")
        void squareMatrixVectorProductZeroMatrix() {
            double[][] matrix = {{0, 0}, {0, 0}};
            double[] vector = {1, 2};
            double[] zeroVector = {0, 0};

            assertArrayEquals(zeroVector, MatrixVectorOperations.squareMatrixVectorProduct(matrix, vector), delta);
        }

        @Test
        @DisplayName("Should return original vector if using unit matrix")
        void squareMatrixVectorProductUnitMatrix() {
            double[][] matrix = {{1, 0}, {0, 1}};
            double[] vector = {1, 2};

            assertArrayEquals(vector, MatrixVectorOperations.squareMatrixVectorProduct(matrix, vector), delta);
        }

        @Test
        @DisplayName("Should return expected vector")
        void squareMatrixVectorProductExpected() {
            double[][] matrix = {{1, 2}, {3, 4}};
            double[] vector = {5, 6};
            double[] expected = {17, 39};

            assertArrayEquals(expected, MatrixVectorOperations.squareMatrixVectorProduct(matrix, vector), delta);
        }
    }
}