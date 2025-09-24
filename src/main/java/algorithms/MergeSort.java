package algorithms;

/*
 * MergeSort implementation with optimizations:
 * - Reusable buffer to avoid repeated allocations
 * - Small-n cutoff to insertion sort for better performance on small arrays
 * - Metrics tracking (comparisons, depth)
 */
public class MergeSort {
    private static final int INSERTION_SORT_CUTOFF = 10;
    
    // Metrics
    private long comparisons = 0;
    private int maxDepth = 0;
    
    /*
     * Public interface for merge sort
     */
    public void sort(int[] array) {
        if (array == null || array.length <= 1) return;
        
        // Reset metrics
        comparisons = 0;
        maxDepth = 0;
        
        // Create reusable buffer
        int[] buffer = new int[array.length];
        
        // Start recursive sorting
        mergeSort(array, buffer, 0, array.length - 1, 0);
    }
    
    /*
     * Recursive merge sort with optimizations
     */
    private void mergeSort(int[] array, int[] buffer, int left, int right, int depth) {
        // Track maximum recursion depth
        maxDepth = Math.max(maxDepth, depth);
        
        // Base case: small arrays use insertion sort
        if (right - left + 1 <= INSERTION_SORT_CUTOFF) {
            insertionSort(array, left, right);
            return;
        }
        
        // Divide
        int mid = left + (right - left) / 2;
        
        // Conquer
        mergeSort(array, buffer, left, mid, depth + 1);
        mergeSort(array, buffer, mid + 1, right, depth + 1);
        
        // Merge
        merge(array, buffer, left, mid, right);
    }
    
    /*
     * Merge two sorted subarrays using buffer
     */
    private void merge(int[] array, int[] buffer, int left, int mid, int right) {
        // Copy left part to buffer
        for (int i = left; i <= mid; i++) {
            buffer[i] = array[i];
        }
        
        // Copy right part to buffer in reverse order (optimization)
        for (int i = mid + 1; i <= right; i++) {
            buffer[right + mid + 1 - i] = array[i];
        }
        
        int i = left;      // Left pointer
        int j = right;     // Right pointer (reverse)
        
        // Merge back to original array
        for (int k = left; k <= right; k++) {
            comparisons++;
            if (buffer[i] <= buffer[j]) {
                array[k] = buffer[i++];
            } else {
                array[k] = buffer[j--];
            }
        }
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
    
    // Getters for metrics
    public long getComparisons() { return comparisons; }
    public int getMaxDepth() { return maxDepth; }
}
