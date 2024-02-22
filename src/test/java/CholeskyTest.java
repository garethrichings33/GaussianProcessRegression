import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CholeskyTest {
    final double delta = 1.0E-12;

    @Nested
    class choleskyTests{
        @Test
        @DisplayName("Should throw an exception if matrix is not square")
        void choleskyNotSquare(){
            double[][] matrix = {{1, 0, 0}, {0, 1, 0}};

            assertThrows(IllegalArgumentException.class, () -> {
                Cholesky.cholesky(matrix);
            });
        }

        @Test
        @DisplayName("Should throw an exception if matrix is not symmetric")
        void choleskyNotSymmetric(){
            double[][] matrix = {{1, 2}, {3, 4}};

            assertThrows(IllegalArgumentException.class, () -> {
                Cholesky.cholesky(matrix);
            });
        }

        @Test
        @DisplayName("Should throw an exception if matrix is not positive definite")
        void choleskyNotPositiveDefinite(){
            double[][] matrix = {{1, 1}, {1, 1}};

            assertThrows(IllegalStateException.class, () -> {
                Cholesky.cholesky(matrix);
            });
        }

        @Test
        @DisplayName("Passing a diagonal matrix should return the square root of the matrix")
        void choleskyDiagonalMatrix(){
            double[][] matrix = {{1, 0, 0}, {0, 2, 0}, {0, 0, 3}};

            Cholesky.cholesky(matrix);

            assertEquals(1, matrix[0][0], delta);
            assertEquals(0, matrix[1][0], delta);
            assertEquals(Math.sqrt(2), matrix[1][1], delta);
            assertEquals(0, matrix[2][0], delta);
            assertEquals(0, matrix[2][1], delta);
            assertEquals(Math.sqrt(3), matrix[2][2], delta);
        }

        @Test
        @DisplayName("Should return the correct decomposition")
        void cholesky2By2Matrix(){
            double[][] matrix = {{1.0, 0.5}, {0.5, 1.0}};

            Cholesky.cholesky(matrix);

            assertEquals(1.0, matrix[0][0], delta);
            assertEquals(0.5, matrix[1][0], delta);
            assertEquals(Math.sqrt(3)/2, matrix[1][1], delta);
        }

        @Test
        @DisplayName("Should return the correct decomposition")
        void cholesky3By3Matrix(){
            double[][] matrix = {{1.00, 0.50, 0.25},
                                 {0.50, 1.00, 0.10},
                                 {0.25, 0.10, 1.00}};

            Cholesky.cholesky(matrix);

            assertEquals(1.0, matrix[0][0], delta);
            assertEquals(0.5, matrix[1][0], delta);
            assertEquals(0.25, matrix[2][0], delta);
            assertEquals(Math.sqrt(3)/2, matrix[1][1], delta);
            assertEquals(-1.0/(20.0 * Math.sqrt(3)), matrix[2][1], delta);
            assertEquals(Math.sqrt(281.0/300.0), matrix[2][2], delta);
        }
    }

    @Nested
    class solveCholeskyTests{
        @Test
        @DisplayName("Should throw an exception if matrix is not square")
        void solveCholeskyNotSquare(){
            double[][] matrix = {{1, 0, 0}, {0, 1, 0}};
            double[] input = {1, 0};

            assertThrows(IllegalArgumentException.class, () -> {
                Cholesky.solveCholesky(matrix, input);
            });
        }

        @Test
        @DisplayName("Should throw an exception if dimensions of matrix and vector do not match")
        void solveCholeskyDimensionsDoNotMatch(){
            double[][] matrix = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
            double[] input = {1, 0};

            assertThrows(IllegalStateException.class, () -> {
                Cholesky.solveCholesky(matrix, input);
            });
        }

        @Test
        @DisplayName("Should return the correct solution vector (2x2)")
        void solveCholeskyCorrectOutput2By2(){
            double[][] matrix = {{1.0, 0.5}, {0.5, 1.0,}};
            double[] input = {1, 2};
            double[] expected = {0.0, 2.0};

            Cholesky.cholesky(matrix);
            var output = Cholesky.solveCholesky(matrix, input);

            assertArrayEquals(expected, output, delta);
        }

        @Test
        @DisplayName("Should return the correct solution vector (3x3)")
        void solveCholeskyCorrectOutput3By3(){
            double[][] matrix = {{1.00, 0.50, 0.25},
                    {0.50, 1.00, 0.10},
                    {0.25, 0.10, 1.00}};
            double[] input = {1, 2, 3};
            double[] expected = {-224.0/281.0, 590.0/281.0, 840.0/281.0};

            Cholesky.cholesky(matrix);
            var output = Cholesky.solveCholesky(matrix, input);

            assertArrayEquals(expected, output, delta);
        }
    }
}
