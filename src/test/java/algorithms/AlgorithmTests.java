package algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/*
 * Comprehensive tests for all implemented algorithms
 */
public class AlgorithmTests {

    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(42); // Fixed seed for reproducible tests
    }

    // =============== MergeSort Tests ===============

    @Test
    void testMergeSortBasic() {
        MergeSort sorter = new MergeSort();
        int[] array = {5, 2, 8, 1, 9, 3};
        int[] expected = {1, 2, 3, 5, 8, 9};

        sorter.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void testMergeSortRandom() {
        MergeSort sorter = new MergeSort();

        for (int size : new int[]{10, 100, 1000}) {
            int[] array = generateRandomArray(size);
            int[] expected = array.clone();
            Arrays.sort(expected);

            sorter.sort(array);
            assertArrayEquals(expected, array);

            // Check depth is reasonable (should be close to log2(n))
            assertTrue(sorter.getMaxDepth() <= Math.ceil(Math.log(size) / Math.log(2)) + 5);
        }
    }

    @Test
    void testMergeSortEdgeCases() {
        MergeSort sorter = new MergeSort();

        // Empty array
        int[] empty = {};
        sorter.sort(empty);
        assertEquals(0, empty.length);

        // Single element
        int[] single = {42};
        sorter.sort(single);
        assertArrayEquals(new int[]{42}, single);

        // Already sorted
        int[] sorted = {1, 2, 3, 4, 5};
        sorter.sort(sorted);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, sorted);

        // Reverse sorted
        int[] reverse = {5, 4, 3, 2, 1};
        sorter.sort(reverse);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, reverse);
    }

    // =============== QuickSort Tests ===============

    @Test
    void testQuickSortBasic() {
        QuickSort sorter = new QuickSort();
        int[] array = {5, 2, 8, 1, 9, 3};
        int[] expected = {1, 2, 3, 5, 8, 9};

        sorter.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void testQuickSortDepthBound() {
        QuickSort sorter = new QuickSort();

        for (int size : new int[]{100, 1000, 10000}) {
            int[] array = generateRandomArray(size);

            sorter.sort(array);

            // Check array is sorted
            assertTrue(isSorted(array));

            // Check depth is bounded (should be ≈ 2*log2(n) + constant)
            int expectedMaxDepth = 2 * (int) Math.ceil(Math.log(size) / Math.log(2)) + 10;
            assertTrue(sorter.getMaxDepth() <= expectedMaxDepth,
                    String.format("Depth %d exceeded expected %d for size %d",
                            sorter.getMaxDepth(), expectedMaxDepth, size));
        }
    }

    @Test
    void testQuickSortWorstCase() {
        QuickSort sorter = new QuickSort();

        // Test with many duplicates (potential worst case)
        int[] duplicates = new int[1000];
        Arrays.fill(duplicates, 5);
        duplicates[0] = 1;
        duplicates[999] = 10;

        sorter.sort(duplicates);
        assertTrue(isSorted(duplicates));
    }

    // =============== DeterministicSelect Tests ===============

    @Test
    void testSelectBasic() {
        DeterministicSelect selector = new DeterministicSelect();
        int[] array = {5, 2, 8, 1, 9, 3};

        // Test finding different k-th elements
        assertEquals(1, selector.select(array, 0)); // minimum
        assertEquals(3, selector.select(array, 2)); // 3rd smallest
        assertEquals(9, selector.select(array, 5)); // maximum
    }

    @Test
    void testSelectRandomArrays() {
        DeterministicSelect selector = new DeterministicSelect();

        for (int trial = 0; trial < 100; trial++) {
            int[] array = generateRandomArray(100);
            int[] sorted = array.clone();
            Arrays.sort(sorted);

            for (int k = 0; k < array.length; k += 10) {
                int result = selector.select(array, k);
                assertEquals(sorted[k], result,
                        String.format("Failed for k=%d in trial %d", k, trial));
            }
        }
    }

    @Test
    void testSelectEdgeCases() {
        DeterministicSelect selector = new DeterministicSelect();

        // Single element
        assertEquals(42, selector.select(new int[]{42}, 0));

        // Two elements
        assertEquals(1, selector.select(new int[]{2, 1}, 0));
        assertEquals(2, selector.select(new int[]{2, 1}, 1));

        // Test with duplicates
        int[] duplicates = {5, 5, 1, 5, 2, 5};
        assertEquals(1, selector.select(duplicates, 0));
        assertEquals(2, selector.select(duplicates, 1));
        assertEquals(5, selector.select(duplicates, 2));
    }

    // =============== ClosestPair Tests ===============

    @Test
    void testClosestPairBasic() {
        ClosestPair finder = new ClosestPair();

        ClosestPair.Point[] points = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(1, 0),
                new ClosestPair.Point(3, 0),
                new ClosestPair.Point(0, 1)
        };

        ClosestPair.Result result = finder.findClosestPair(points);
        assertEquals(1.0, result.distance, 1e-9);
    }

    @Test
    void testClosestPairAgainstBruteForce() {
        ClosestPair finder = new ClosestPair();

        // Test with small arrays where we can verify against brute force
        for (int trial = 0; trial < 10; trial++) {
            ClosestPair.Point[] points = generateRandomPoints(50);

            ClosestPair.Result result = finder.findClosestPair(points);
            ClosestPair.Result bruteForceResult = bruteForceClosestPair(points);

            assertEquals(bruteForceResult.distance, result.distance, 1e-9,
                    "Mismatch in trial " + trial);
        }
    }

    @Test
    void testClosestPairEdgeCases() {
        ClosestPair finder = new ClosestPair();

        // Two points
        ClosestPair.Point[] twoPoints = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(3, 4)
        };
        ClosestPair.Result result = finder.findClosestPair(twoPoints);
        assertEquals(5.0, result.distance, 1e-9);

        // Three collinear points
        ClosestPair.Point[] collinear = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(1, 0),
                new ClosestPair.Point(2, 0)
        };
        result = finder.findClosestPair(collinear);
        assertEquals(1.0, result.distance, 1e-9);
    }

    // =============== Helper Methods ===============

    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000);
        }
        return array;
    }

    private ClosestPair.Point[] generateRandomPoints(int count) {
        ClosestPair.Point[] points = new ClosestPair.Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new ClosestPair.Point(random.nextDouble() * 100,
                    random.nextDouble() * 100);
        }
        return points;
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i-1]) return false;
        }
        return true;
    }

    private ClosestPair.Result bruteForceClosestPair(ClosestPair.Point[] points) {
        double minDist = Double.MAX_VALUE;
        ClosestPair.Point best1 = null, best2 = null;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = points[i].distanceTo(points[j]);
                if (dist < minDist) {
                    minDist = dist;
                    best1 = points[i];
                    best2 = points[j];
                }
            }
        }

        return new ClosestPair.Result(best1, best2, minDist);
    }
}
