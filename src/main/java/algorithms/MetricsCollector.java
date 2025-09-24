package algorithms;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * Metrics collection and benchmarking for all algorithms
 */
public class MetricsCollector {

    public static class Metrics {
        public final String algorithm;
        public final int inputSize;
        public final long executionTimeNanos;
        public final int maxDepth;
        public final long comparisons;
        public final String inputType;

        public Metrics(String algorithm, int inputSize, long executionTimeNanos,
                       int maxDepth, long comparisons, String inputType) {
            this.algorithm = algorithm;
            this.inputSize = inputSize;
            this.executionTimeNanos = executionTimeNanos;
            this.maxDepth = maxDepth;
            this.comparisons = comparisons;
            this.inputType = inputType;
        }

        @Override
        public String toString() {
            return String.format("%s,%d,%d,%d,%d,%s",
                    algorithm, inputSize, executionTimeNanos, maxDepth, comparisons, inputType);
        }
    }

    private List<Metrics> allMetrics = new ArrayList<>();
    private Random random = new Random(42);

    /*
     * Run comprehensive benchmarks for all algorithms
     */
    public void runAllBenchmarks() {
        System.out.println("Starting comprehensive benchmarks...");

        // Test different input sizes
        int[] sizes = {10, 50, 100, 500, 1000, 2000, 5000, 10000};

        for (int size : sizes) {
            System.out.printf("Testing size %d...\n", size);

            benchmarkSortingAlgorithms(size);
            benchmarkSelect(size);
            if (size <= 2000) { // Limit for closest pair to avoid long runs
                benchmarkClosestPair(size);
            }
        }

        System.out.println("Benchmarks completed!");
    }

    /*
     * Benchmark MergeSort and QuickSort
     */
    private void benchmarkSortingAlgorithms(int size) {
        // Test different input types
        String[] inputTypes = {"random", "sorted", "reverse", "duplicates"};

        for (String inputType : inputTypes) {
            int[] array = generateArray(size, inputType);

            // Test MergeSort
            benchmarkMergeSort(array.clone(), inputType);

            // Test QuickSort
            benchmarkQuickSort(array.clone(), inputType);
        }
    }

    private void benchmarkMergeSort(int[] array, String inputType) {
        MergeSort sorter = new MergeSort();

        long startTime = System.nanoTime();
        sorter.sort(array);
        long endTime = System.nanoTime();

        allMetrics.add(new Metrics("MergeSort", array.length,
                endTime - startTime, sorter.getMaxDepth(),
                sorter.getComparisons(), inputType));
    }

    private void benchmarkQuickSort(int[] array, String inputType) {
        QuickSort sorter = new QuickSort();

        long startTime = System.nanoTime();
        sorter.sort(array);
        long endTime = System.nanoTime();

        allMetrics.add(new Metrics("QuickSort", array.length,
                endTime - startTime, sorter.getMaxDepth(),
                sorter.getComparisons(), inputType));
    }

    /*
     * Benchmark DeterministicSelect
     */
    private void benchmarkSelect(int size) {
        int[] array = generateArray(size, "random");
        DeterministicSelect selector = new DeterministicSelect();

        // Test finding median
        int k = size / 2;

        long startTime = System.nanoTime();
        selector.select(array, k);
        long endTime = System.nanoTime();

        allMetrics.add(new Metrics("Select", size,
                endTime - startTime, selector.getMaxDepth(),
                selector.getComparisons(), "random"));
    }

    /*
     * Benchmark ClosestPair
     */
    private void benchmarkClosestPair(int size) {
        ClosestPair.Point[] points = generateRandomPoints(size);
        ClosestPair finder = new ClosestPair();

        long startTime = System.nanoTime();
        finder.findClosestPair(points);
        long endTime = System.nanoTime();

        allMetrics.add(new Metrics("ClosestPair", size,
                endTime - startTime, finder.getMaxDepth(),
                finder.getComparisons(), "random"));
    }

    /*
     * Generate arrays of different types for testing
     */
    private int[] generateArray(int size, String type) {
        int[] array = new int[size];

        switch (type) {
            case "random":
                for (int i = 0; i < size; i++) {
                    array[i] = random.nextInt(size * 10);
                }
                break;

            case "sorted":
                for (int i = 0; i < size; i++) {
                    array[i] = i;
                }
                break;

            case "reverse":
                for (int i = 0; i < size; i++) {
                    array[i] = size - i;
                }
                break;

            case "duplicates":
                int numUnique = Math.max(1, size / 10);
                for (int i = 0; i < size; i++) {
                    array[i] = random.nextInt(numUnique);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown array type: " + type);
        }

        return array;
    }

    private ClosestPair.Point[] generateRandomPoints(int count) {
        ClosestPair.Point[] points = new ClosestPair.Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new ClosestPair.Point(
                    random.nextDouble() * 1000,
                    random.nextDouble() * 1000
            );
        }
        return points;
    }

    /*
     * Save metrics to CSV file
     */
    public void saveMetricsToCSV(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("Algorithm,InputSize,ExecutionTimeNanos,MaxDepth,Comparisons,InputType\n");

            // Write data
            for (Metrics metrics : allMetrics) {
                writer.write(metrics.toString() + "\n");
            }
        }

        System.out.println("Metrics saved to " + filename);
    }

    /*
     * Print summary statistics
     */
    public void printSummary() {
        System.out.println("\n=== BENCHMARK SUMMARY ===");

        // Group by algorithm
        allMetrics.stream()
                .collect(java.util.stream.Collectors.groupingBy(m -> m.algorithm))
                .forEach((algorithm, metrics) -> {
                    System.out.printf("\n%s:\n", algorithm);

                    // Find average execution time for largest input
                    metrics.stream()
                            .filter(m -> m.inputType.equals("random"))
                            .max((m1, m2) -> Integer.compare(m1.inputSize, m2.inputSize))
                            .ifPresent(maxMetric ->
                                    System.out.printf("  Largest test (n=%d): %.2f ms, depth=%d, comparisons=%d\n",
                                            maxMetric.inputSize,
                                            maxMetric.executionTimeNanos / 1_000_000.0,
                                            maxMetric.maxDepth,
                                            maxMetric.comparisons)
                            );
                });
    }

    /*
     * Validate algorithm correctness
     */
    public void validateAlgorithms() {
        System.out.println("Validating algorithm correctness...");

        // Test sorting algorithms
        for (int size : new int[]{100, 1000}) {
            int[] original = generateArray(size, "random");

            // Test MergeSort
            int[] mergeSorted = original.clone();
            new MergeSort().sort(mergeSorted);

            // Test QuickSort
            int[] quickSorted = original.clone();
            new QuickSort().sort(quickSorted);

            // Test against Arrays.sort
            int[] javaSorted = original.clone();
            Arrays.sort(javaSorted);

            assert Arrays.equals(mergeSorted, javaSorted) : "MergeSort failed validation";
            assert Arrays.equals(quickSorted, javaSorted) : "QuickSort failed validation";
        }

        // Test Select algorithm
        for (int trial = 0; trial < 10; trial++) {
            int[] array = generateArray(100, "random");
            int[] sorted = array.clone();
            Arrays.sort(sorted);

            DeterministicSelect selector = new DeterministicSelect();
            for (int k = 0; k < array.length; k += 10) {
                int result = selector.select(array.clone(), k);
                assert result == sorted[k] : "Select algorithm failed validation";
            }
        }

        System.out.println("All algorithms passed validation!");
    }

    /*
     * Main method for running benchmarks
     */
    public static void main(String[] args) {
        MetricsCollector collector = new MetricsCollector();

        try {
            // Validate algorithms first
            collector.validateAlgorithms();

            // Run benchmarks
            collector.runAllBenchmarks();

            // Save results
            collector.saveMetricsToCSV("benchmark_results.csv");

            // Print summary
            collector.printSummary();

        } catch (IOException e) {
            System.err.println("Error saving metrics: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during benchmarking: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
