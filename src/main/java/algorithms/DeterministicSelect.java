package algorithms;

/*
 * Deterministic Select algorithm (Median of Medians)
 * Finds k-th the smallest element in O(n) time
 */

public class DeterministicSelect {
    private static final int GROUP_SIZE = 5;

    // Metrics
    private long comparisons = 0;
    private int maxDepth = 0;

    /*
     * Find k-th smallest element (0-indexed)
     */
    public int select(int[] array, int k) {
        if (array == null || k < 0 || k >= array.length) {
            throw new IllegalArgumentException("Invalid input");
        }

        // Reset metrics
        comparisons = 0;
        maxDepth = 0;

        // Create a copy to avoid modifying original array
        int[] copy = array.clone();
        return select(copy, 0, copy.length - 1, k, 0);
    }

    /*
     * Recursive select implementation
     */
    private int select(int[] array, int left, int right, int k, int depth) {
        maxDepth = Math.max(maxDepth, depth);

        // Base case: small arrays
        if (right - left + 1 <= GROUP_SIZE) {
            insertionSort(array, left, right);
            return array[left + k];
        }

        // Step 1: Divide into groups of 5 and find median of each group
        int numGroups = (right - left + 1 + GROUP_SIZE - 1) / GROUP_SIZE;
        int[] medians = new int[numGroups];

        for (int i = 0; i < numGroups; i++) {
            int groupLeft = left + i * GROUP_SIZE;
            int groupRight = Math.min(groupLeft + GROUP_SIZE - 1, right);

            // Sort this group and find median
            insertionSort(array, groupLeft, groupRight);
            int medianIndex = groupLeft + (groupRight - groupLeft) / 2;
            medians[i] = array[medianIndex];
        }

        // Step 2: Find median of medians recursively
        int medianOfMedians = select(medians, 0, medians.length - 1,
                medians.length / 2, depth + 1);

        // Step 3: Partition around median of medians
        int pivotIndex = findAndMovePivot(array, left, right, medianOfMedians);
        int partitionIndex = partition(array, left, right, pivotIndex);

        // Step 4: Recurse on appropriate side
        int position = partitionIndex - left;
        if (position == k) {
            return array[partitionIndex];
        } else if (position > k) {
            return select(array, left, partitionIndex - 1, k, depth + 1);
        } else {
            return select(array, partitionIndex + 1, right, k - position - 1, depth + 1);
        }
    }

    /*
     * Find pivot value in array and move to end
     */
    private int findAndMovePivot(int[] array, int left, int right, int pivotValue) {
        for (int i = left; i <= right; i++) {
            if (array[i] == pivotValue) {
                swap(array, i, right);
                return right;
            }
        }
        // Should not reach here with correct implementation
        return right;
    }

    /*
     * Partition array around pivot at given index
     */
    private int partition(int[] array, int left, int right, int pivotIndex) {
        int pivot = array[pivotIndex];
        swap(array, pivotIndex, right); // Move pivot to end

        int i = left - 1;

        for (int j = left; j < right; j++) {
            comparisons++;
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, right);
        return i + 1;
    }

    /*
     * Insertion sort for small arrays
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
            if (j >= left) comparisons++;

            array[j + 1] = key;
        }
    }

    /*
     * Swap two elements
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
