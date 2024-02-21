package test.java;

import static main.java.Cholesky.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CholeskyTest {
    @Nested
    class choleskyTests{
        @Test
        @DisplayName("Should throw an exception if matrix is not square")
        void choleskyNotSquare(){
            double[][] matrix = {{1, 0, 0}, {0, 1, 0}};

            assertThrows(IllegalArgumentException.class, () -> {
                cholesky(matrix);
            });
        }

        @Test
        @DisplayName("Should throw an exception if matrix is not symmetric")
        void choleskyNotSymmetric(){
            double[][] matrix = {{1, 2}, {3, 4}};

            assertThrows(IllegalArgumentException.class, () -> {
                cholesky(matrix);
            });
        }

        @Test
        @DisplayName("Should throw an exception if matrix is not positive definite")
        void choleskyNotPositiveDefinite(){
            double[][] matrix = {{1, 1}, {1, 1}};

            assertThrows(IllegalStateException.class, () -> {
                cholesky(matrix);
            });
        }

        @Test
        @DisplayName("Passing a diagonal matrix should return the square root of the matrix")
        void choleskyDiagonalMatrix(){
            double[][] matrix = {{1, 0, 0}, {0, 2, 0}, {0, 0, 3}};

            cholesky(matrix);

            assertEquals(1, matrix[0][0]);
            assertEquals(0, matrix[1][0]);
            assertEquals(Math.sqrt(2), matrix[1][1]);
            assertEquals(0, matrix[2][0]);
            assertEquals(0, matrix[2][1]);
            assertEquals(Math.sqrt(3), matrix[2][2]);
        }

        @Test
        @DisplayName("Should return the correct decomposition")
        void cholesky2By2Matrix(){
            double[][] matrix = {{1.0, 0.5}, {0.5, 1.0}};

            cholesky(matrix);

            assertEquals(1.0, matrix[0][0]);
            assertEquals(0.5, matrix[1][0]);
            assertEquals(Math.sqrt(3)/2, matrix[1][1]);
        }
    }
}
