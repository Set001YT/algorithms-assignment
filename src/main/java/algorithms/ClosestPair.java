package algorithms;

import java.util.*;

/*
 * Closest Pair of Points algorithm using Divide and Conquer
 * Time complexity: O(n log n)
 */

public class ClosestPair {

    // Metrics
    private long comparisons = 0;
    private int maxDepth = 0;

    /*
     * Point class to represent 2D coordinates
     */
    public static class Point {
        public final double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distanceTo(Point other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }

    /*
     * Result class to hold the closest pair and distance
     */
    public static class Result {
        public final Point p1, p2;
        public final double distance;

        public Result(Point p1, Point p2, double distance) {
            this.p1 = p1;
            this.p2 = p2;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("Distance: %.6f between %s and %s", distance, p1, p2);
        }
    }

    /*
     * Find the closest pair of points
     */
    public Result findClosestPair(Point[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }

        // Reset metrics
        comparisons = 0;
        maxDepth = 0;

        // Sort points by x-coordinate
        Point[] sortedByX = points.clone();
        Arrays.sort(sortedByX, (p1, p2) -> Double.compare(p1.x, p2.x));

        // Create array sorted by y-coordinate for strip checking
        Point[] sortedByY = points.clone();
        Arrays.sort(sortedByY, (p1, p2) -> Double.compare(p1.y, p2.y));

        return closestPairRec(sortedByX, sortedByY, 0);
    }

    /*
     * Recursive divide and conquer implementation
     */
    private Result closestPairRec(Point[] pointsByX, Point[] pointsByY, int depth) {
        maxDepth = Math.max(maxDepth, depth);

        int n = pointsByX.length;

        // Base case: use brute force for small arrays
        if (n <= 3) {
            return bruteForce(pointsByX);
        }

        // Divide: find middle point
        int mid = n / 2;
        Point midPoint = pointsByX[mid];

        // Split points by x coordinate
        Point[] leftByX = Arrays.copyOfRange(pointsByX, 0, mid);
        Point[] rightByX = Arrays.copyOfRange(pointsByX, mid, n);

        // Split points by y coordinate
        Point[] leftByY = new Point[mid];
        Point[] rightByY = new Point[n - mid];
        int leftIdx = 0, rightIdx = 0;

        for (Point p : pointsByY) {
            if (p.x <= midPoint.x && leftIdx < mid) {
                leftByY[leftIdx++] = p;
            } else {
                rightByY[rightIdx++] = p;
            }
        }

        // Conquer: recursively find the closest pairs in both halves
        Result leftResult = closestPairRec(leftByX, leftByY, depth + 1);
        Result rightResult = closestPairRec(rightByX, rightByY, depth + 1);

        // Find minimum distance from both halves
        Result minResult = (leftResult.distance <= rightResult.distance) ? leftResult : rightResult;
        double minDist = minResult.distance;

        // Check the strip around the dividing line
        List<Point> strip = new ArrayList<>();
        for (Point p : pointsByY) {
            if (Math.abs(p.x - midPoint.x) < minDist) {
                strip.add(p);
            }
        }

        // Check points in strip - at most 7 points need to be checked for each point
        Result stripResult = checkStrip(strip, minDist);

        return (stripResult != null && stripResult.distance < minDist) ? stripResult : minResult;
    }

    /*
     * Check strip for closer pairs - optimized to check at most 7 neighbors
     */
    private Result checkStrip(List<Point> strip, double minDist) {
        Result best = null;

        for (int i = 0; i < strip.size(); i++) {
            Point p1 = strip.get(i);

            // Check at most next 7 points (proven mathematical bound)
            for (int j = i + 1; j < strip.size() && j < i + 8; j++) {
                Point p2 = strip.get(j);

                // Early termination: if y-difference > minDist, no need to check further
                if (p2.y - p1.y >= minDist) break;

                comparisons++;
                double dist = p1.distanceTo(p2);
                if (dist < minDist) {
                    minDist = dist;
                    best = new Result(p1, p2, dist);
                }
            }
        }

        return best;
    }

    /*
     * Brute force method for small arrays
     */
    private Result bruteForce(Point[] points) {
        double minDist = Double.MAX_VALUE;
        Point best1 = null, best2 = null;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                comparisons++;
                double dist = points[i].distanceTo(points[j]);
                if (dist < minDist) {
                    minDist = dist;
                    best1 = points[i];
                    best2 = points[j];
                }
            }
        }

        return new Result(best1, best2, minDist);
    }

    // Getters for metrics
    public long getComparisons() { return comparisons; }
    public int getMaxDepth() { return maxDepth; }
}
