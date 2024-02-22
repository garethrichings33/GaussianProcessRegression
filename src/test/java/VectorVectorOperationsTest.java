import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorVectorOperationsTest {

    @Nested
    class dotProductTests {
        @Test
        @DisplayName("Should return error if vectors of different length")
        void dotProductWrongVectorLengths() {
            double[] vector1 = {1, 2, 3};
            double[] vector2 = {1, 2};

            assertThrows(IllegalStateException.class,
                    () -> {
                        VectorVectorOperations.dotProduct(vector1, vector2);
                    }
            );
        }

        @Test
        @DisplayName("Should return 0 if passed zero-vectors")
        void dotProductZeroVectors() {
            double[] vector1 = {0, 0};
            double[] vector2 = {0, 0};

            assertEquals(0.0, VectorVectorOperations.dotProduct(vector1, vector2));
        }

        @Test
        @DisplayName("Should return 0 if passed orthogonal vectors")
        void dotProductOrthogonalVectors() {
            double[] vector1 = {1, 0};
            double[] vector2 = {0, 1};
            double[] vector3 = {1, 1};
            double[] vector4 = {1, -1};

            assertEquals(0.0, VectorVectorOperations.dotProduct(vector1, vector2));
            assertEquals(0.0, VectorVectorOperations.dotProduct(vector2, vector1));
            assertEquals(0.0, VectorVectorOperations.dotProduct(vector3, vector4));
        }

        @Test
        @DisplayName("Should return 1 if passed parallel unit-vectors")
        void dotProductParallelUnitVectors() {
            double[] vector1 = {1, 0};
            double[] vector2 = {1, 0};
            double[] vector3 = {0, 1};
            double[] vector4 = {0, 1};

            assertEquals(1.0, VectorVectorOperations.dotProduct(vector1, vector2));
            assertEquals(1.0, VectorVectorOperations.dotProduct(vector3, vector4));
        }

        @Test
        @DisplayName("Should return -2 if passed anti-parallel unit-vectors")
        void dotProductAntiParallelUnitVectors() {
            double[] vector1 = {1, 1};
            double[] vector2 = {-1, -1};

            assertEquals(-2.0, VectorVectorOperations.dotProduct(vector1, vector2));
        }
    }

    @Nested
    class differenceTests {
        @Test
        @DisplayName("Should return error if vectors of different length")
        void differenceWrongVectorLengths() {
            double[] vector1 = {1, 2, 3};
            double[] vector2 = {1, 2};

            assertThrows(IllegalStateException.class,
                    () -> {
                        VectorVectorOperations.difference(vector1, vector2);
                    }
            );
        }

        @Test
        @DisplayName("Should return 0 if same vector")
        void differenceSameVector() {
            double[] vector1 = {1, 2};
            double[] expected = {0, 0};

            assertArrayEquals(expected, VectorVectorOperations.difference(vector1, vector1));
        }

        @Test
        @DisplayName("Should correctly subtract two non-zero vectors")
        void differenceDifferentVectors() {
            double[] vector1 = {1, 2, 3};
            double[] vector2 = {-2, 4, 7};
            double[] expected = {3, -2, -4};
            double[] expectedMinus = {-3, 2, 4};

            assertArrayEquals(expected, VectorVectorOperations.difference(vector1, vector2));
            assertArrayEquals(expectedMinus, VectorVectorOperations.difference(vector2, vector1));
        }
    }
}