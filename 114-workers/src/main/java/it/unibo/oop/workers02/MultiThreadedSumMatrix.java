package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a standard implementation of the calculation.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThread;

    /**
     * @param nThread number of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int nThread) {
        this.nThread = nThread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final Position startPosition;
        private final int nElements;
        private long result;

        Worker(final double[][] matrix, final Position startPosition, final int nElements) {
            super();
            this.matrix = matrix.clone();
            this.startPosition = startPosition;
            this.nElements = nElements;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            long nElementsChecked = 0;
            final int nRows = this.matrix.length;
            final int nColumns = this.matrix[0].length;

            for (int i = this.startPosition.i; i < nRows && nElementsChecked < this.nElements; i++) {
                for (int j = i == this.startPosition.i ? this.startPosition.j : 0; j < nColumns
                        && nElementsChecked < this.nElements; j++) {
                    this.result += this.matrix[i][j];
                    nElementsChecked++;
                }
            }

            System.out.println("Working from position " + startPosition + " for " + nElementsChecked + " elements.");
        }

        public long getResult() {
            return this.result;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        if (matrix.length == 0) {
            throw new IllegalArgumentException("The matrix must have at least 1 row.");
        }

        final int nRows = matrix.length;
        final int nColumns = matrix[0].length;
        final int matrixSize = nRows * nColumns;

        final int workerSize = matrixSize % nThread + matrixSize / nThread;

        // Build a list of workers
        final List<Worker> workers = new ArrayList<>(nThread);
        for (int start = 0; start < matrixSize; start += workerSize) {
            workers.add(new Worker(matrix, Position.fromIndex(start, nRows, nColumns), workerSize));
        }

        // Start them
        for (final Worker w : workers) {
            w.start();
        }

        /*
         * Wait for every one of them to finish.
         * This operation is _way_ better done by using barriers and latches,
         * and the whole operation would be better done with futures.
         */
        long sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        // Return the sum
        return sum;
    }

    /**
     * Represents a pair of indexes in a matrix.
     */
    private static final class Position {
        private final int i;
        private final int j;

        private Position(final int i, final int j) {
            this.i = i;
            this.j = j;
        }

        public static Position fromIndex(final int index, final int rows, final int columns) {
            return new Position(index / columns, index % rows);
        }

        @Override
        public String toString() {
            return "(" + i + ", " + j + ")";
        }
    }
}
