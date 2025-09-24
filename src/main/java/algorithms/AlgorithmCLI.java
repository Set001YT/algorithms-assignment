package algorithms;

import java.util.*;

/*
 * Command Line Interface for testing algorithms
 */
public class AlgorithmCLI {

    private Scanner scanner = new Scanner(System.in);
    private Random random = new Random();

    public static void main(String[] args) {
        AlgorithmCLI cli = new AlgorithmCLI();
        cli.run();
    }

    public void run() {
        System.out.println("=== Algorithm Testing CLI ===");
        System.out.println("Implemented algorithms: MergeSort, QuickSort, Select, ClosestPair");

        while (true) {
            printMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    testSortingAlgorithms();
                    break;
                case 2:
                    testSelectAlgorithm();
                    break;
                case 3:
                    testClosestPairAlgorithm();
                    break;
                case 4:
                    runBenchmarks();
                    break;
                case 5:
                    compareAlgorithms();
                    break;
                case 0:
                    System.out.println("Goodbye! Have a nice day!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Test Sorting Algorithms (MergeSort & QuickSort)");
        System.out.println("2. Test Select Algorithm");
        System.out.println("3. Test Closest Pair Algorithm");
        System.out.println("4. Run Benchmarks");
        System.out.println("5. Compare Algorithms");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void testSortingAlgorithms() {
        System.out.print("Enter array size (or 'r' for random size): ");
        String input = scanner.nextLine().trim();

        int size;
        if (input.equals("r")) {
            size = random.nextInt(20) + 5; // Random size between 5-24
        } else {
            try {
                size = Integer.parseInt(input);
                if (size <= 0 || size > 10000) {
                    System.out.println("Size should be between 1 and 10000");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
                return;
            }
        }

        // Generate test array
        int[] original = generateRandomArray(size);
        System.out.println("Original array: " + Arrays.toString(Arrays.copyOf(original, Math.min(20, size))));
        if (size > 20) System.out.println("... (showing first 20 elements)");

        // Test MergeSort
        int[] mergeSortArray = original.clone();
        MergeSort mergeSort = new MergeSort();

        long startTime = System.nanoTime();
        mergeSort.sort(mergeSortArray);
        long mergeTime = System.nanoTime() - startTime;

        System.out.println("\nMergeSort Results:");
        System.out.println("  Time: " + (mergeTime / 1_000_000.0) + " ms");
        System.out.println("  Max Depth: " + mergeSort.getMaxDepth());
        System.out.println("  Comparisons: " + mergeSort.getComparisons());
        System.out.println("  Sorted: " + Arrays.toString(Arrays.copyOf(mergeSortArray, Math.min(20, size))));

        // Test QuickSort
        int[] quickSortArray = original.clone();
        QuickSort quickSort = new QuickSort();

        startTime = System.nanoTime();
        quickSort.sort(quickSortArray);
        long quickTime = System.nanoTime() - startTime;

        System.out.println("\nQuickSort Results:");
        System.out.println("  Time: " + (quickTime / 1_000_000.0) + " ms");
        System.out.println("  Max Depth: " + quickSort.getMaxDepth());
        System.out.println("  Comparisons: " + quickSort.getComparisons());
        System.out.println("  Sorted: " + Arrays.toString(Arrays.copyOf(quickSortArray, Math.min(20, size))));

        // Verify correctness
        Arrays.sort(original);
        boolean mergeCorrect = Arrays.equals(mergeSortArray, original);
        boolean quickCorrect = Arrays.equals(quickSortArray, original);

        System.out.println("\nCorrectness Check:");
        System.out.println("  MergeSort: " + (mergeCorrect ? "PASSED" : "FAILED"));
        System.out.println("  QuickSort: " + (quickCorrect ? "PASSED" : "FAILED"));
    }

    private void testSelectAlgorithm() {
        System.out.print("Enter array size: ");
        int size;
        try {
            size = Integer.parseInt(scanner.nextLine().trim());
            if (size <= 0 || size > 10000) {
                System.out.println("Size should be between 1 and 10000");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
            return;
        }

        System.out.print("Enter k (0-indexed, or 'm' for median): ");
        String kInput = scanner.nextLine().trim();

        int k;
        if (kInput.equals("m")) {
            k = size / 2;
        } else {
            try {
                k = Integer.parseInt(kInput);
                if (k < 0 || k >= size) {
                    System.out.println("k should be between 0 and " + (size - 1));
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
                return;
            }
        }

        // Generate test array
        int[] original = generateRandomArray(size);
        System.out.println("Original array: " + Arrays.toString(Arrays.copyOf(original, Math.min(20, size))));

        // Test Select algorithm
        DeterministicSelect selector = new DeterministicSelect();

        long startTime = System.nanoTime();
        int result = selector.select(original.clone(), k);
        long selectTime = System.nanoTime() - startTime;

        // Verify with sorting
        int[] sorted = original.clone();
        Arrays.sort(sorted);
        int expected = sorted[k];

        System.out.println("\nSelect Algorithm Results:");
        System.out.println("  Finding " + k + "-th smallest element (0-indexed)");
        System.out.println("  Result: " + result);
        System.out.println("  Expected: " + expected);
        System.out.println("  Time: " + (selectTime / 1_000_000.0) + " ms");
        System.out.println("  Max Depth: " + selector.getMaxDepth());
        System.out.println("  Comparisons: " + selector.getComparisons());
        System.out.println("  Correctness: " + (result == expected ? "PASSED" : "FAILED"));
    }

    private void testClosestPairAlgorithm() {
        System.out.print("Enter number of points: ");
        int numPoints;
        try {
            numPoints = Integer.parseInt(scanner.nextLine().trim());
            if (numPoints < 2 || numPoints > 1000) {
                System.out.println("Number of points should be between 2 and 1000");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
            return;
        }

        // Generate random points
        ClosestPair.Point[] points = generateRandomPoints(numPoints);
        System.out.println("Generated " + numPoints + " random points");

        if (numPoints <= 10) {
            System.out.println("Points:");
            for (int i = 0; i < points.length; i++) {
                System.out.printf("  P%d: %s\n", i, points[i]);
            }
        }

        // Test Closest Pair algorithm
        ClosestPair finder = new ClosestPair();

        long startTime = System.nanoTime();
        ClosestPair.Result result = finder.findClosestPair(points);
        long findTime = System.nanoTime() - startTime;

        System.out.println("\nClosest Pair Algorithm Results:");
        System.out.println("  " + result);
        System.out.println("  Time: " + (findTime / 1_000_000.0) + " ms");
        System.out.println("  Max Depth: " + finder.getMaxDepth());
        System.out.println("  Comparisons: " + finder.getComparisons());

        // Verify with brute force for small inputs
        if (numPoints <= 100) {
            ClosestPair.Result bruteForceResult = bruteForceClosestPair(points);
            boolean correct = Math.abs(result.distance - bruteForceResult.distance) < 1e-9;
            System.out.println("  Brute force verification: " + (correct ? "PASSED" : "FAILED"));
            if (!correct) {
                System.out.println("  Expected distance: " + bruteForceResult.distance);
            }
        }
    }

    private void runBenchmarks() {
        System.out.println("Running comprehensive benchmarks...");
        System.out.println("This may take a few moments...");

        MetricsCollector collector = new MetricsCollector();

        try {
            collector.validateAlgorithms();
            collector.runAllBenchmarks();
            collector.printSummary();
            collector.saveMetricsToCSV("cli_benchmark_results.csv");
            System.out.println("Results saved to cli_benchmark_results.csv");
        } catch (Exception e) {
            System.out.println("Error during benchmarking: " + e.getMessage());
        }
    }

    private void compareAlgorithms() {
        System.out.println("Comparing MergeSort vs QuickSort");
        System.out.print("Enter array size for comparison: ");

        int size;
        try {
            size = Integer.parseInt(scanner.nextLine().trim());
            if (size <= 0 || size > 50000) {
                System.out.println("Size should be between 1 and 50000");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
            return;
        }

        String[] inputTypes = {"random", "sorted", "reverse"};

        for (String inputType : inputTypes) {
            System.out.println("\n--- " + inputType.toUpperCase() + " INPUT ---");

            int[] array = generateArray(size, inputType);

            // Test MergeSort
            int[] mergeArray = array.clone();
            MergeSort mergeSort = new MergeSort();
            long startTime = System.nanoTime();
            mergeSort.sort(mergeArray);
            long mergeTime = System.nanoTime() - startTime;

            // Test QuickSort
            int[] quickArray = array.clone();
            QuickSort quickSort = new QuickSort();
            startTime = System.nanoTime();
            quickSort.sort(quickArray);
            long quickTime = System.nanoTime() - startTime;

            System.out.printf("MergeSort: %.2f ms, depth=%d, comparisons=%d\n",
                    mergeTime / 1_000_000.0, mergeSort.getMaxDepth(), mergeSort.getComparisons());
            System.out.printf("QuickSort: %.2f ms, depth=%d, comparisons=%d\n",
                    quickTime / 1_000_000.0, quickSort.getMaxDepth(), quickSort.getComparisons());

            double speedup = (double) mergeTime / quickTime;
            System.out.printf("QuickSort is %.2fx %s than MergeSort\n",
                    Math.abs(speedup), speedup > 1 ? "faster" : "slower");
        }
    }

    // Helper methods
    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 2);
        }
        return array;
    }

    private int[] generateArray(int size, String type) {
        int[] array = new int[size];
        switch (type) {
            case "random":
                for (int i = 0; i < size; i++) {
                    array[i] = random.nextInt(size * 2);
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
        }
        return array;
    }

    private ClosestPair.Point[] generateRandomPoints(int count) {
        ClosestPair.Point[] points = new ClosestPair.Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new ClosestPair.Point(
                    random.nextDouble() * 100,
                    random.nextDouble() * 100
            );
        }
        return points;
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
