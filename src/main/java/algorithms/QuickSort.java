package algorithms;

import java.util.Random;

/*
 * Robust QuickSort implementation:
 * - Randomized pivot selection
 * - Recurse on smaller partition, iterate on larger (bounded stack depth)
 * - Metrics tracking
 */

public class QuickSort {
    private static final int INSERTION_SORT_CUTOFF = 10;
    private final Random random = new Random();

    // Metrics
    private long comparisons = 0;
    private int maxDepth = 0;

    /*
     * Public interface for quicksort
     */
    public void sort(int[] array) {
        if (array == null || array.length <= 1) return;

        // Reset metrics
        comparisons = 0;
        maxDepth = 0;

        // Start iterative quicksort with bounded recursion
        quickSort(array, 0, array.length - 1, 0);
    }

    /*
     * QuickSort with tail recursion optimization
     * Recurse on smaller partition, iterate on larger
     */
    private void quickSort(int[] array, int left, int right, int depth) {
        while (left < right) {
            // Track maximum depth
            maxDepth = Math.max(maxDepth, depth);

            // Use insertion sort for small subarrays
            if (right - left + 1 <= INSERTION_SORT_CUTOFF) {
                insertionSort(array, left, right);
                break;
            }

            // Partition with random pivot
            int pivotIndex = partition(array, left, right);

            // Recurse on smaller partition, iterate on larger
            if (pivotIndex - left < right - pivotIndex) {
                // Left partition is smaller
                quickSort(array, left, pivotIndex - 1, depth + 1);
                left = pivotIndex + 1; // Continue with right partition
            } else {
                // Right partition is smaller or equal
                quickSort(array, pivotIndex + 1, right, depth + 1);
                right = pivotIndex - 1; // Continue with left partition
            }
        }
    }

    /*
     * Partition array around random pivot
     * Returns final position of pivot
     */
    private int partition(int[] array, int left, int right) {
        // Choose random pivot and move to end
        int randomIndex = left + random.nextInt(right - left + 1);
        swap(array, randomIndex, right);

        int pivot = array[right];
        int i = left - 1; // Index of smaller element

        for (int j = left; j < right; j++) {
            comparisons++;
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }

        // Place pivot in correct position
        swap(array, i + 1, right);
        return i + 1;
    }

    /*
     * Insertion sort for small subarrays
     */
    private void insertionSort(int[] array, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = array[i];
            int j = i - 1;

            while (j >= left && array[j] > key) {
                comparisons++;
                array[j + 1] = array[j];
                j--;
            }
            if (j >= left) comparisons++; // Count the final comparison

            array[j + 1] = key;
        }
    }

    /*
     * Swap two elements in array
     */
    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // Getters for metrics
    public long getComparisons() { return comparisons; }
    public int getMaxDepth() { return maxDepth; }
}
