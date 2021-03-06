/*
 * An application that visualizes sorting algorithms through bar graphs.
 *
 * Author : Daniel La Rocque
 *
 * Date started : April 28th, 2020
 *
 * This project is licensed under the MIT License.
 * See LICENSE.txt for more details
 *
 * Instructions on how to use the application are included in README.md
 */
package com.example.algorithmvisualizer;

import java.util.*;
import javax.swing.SwingWorker;

public class ArrSorting extends SwingWorker<Void, Integer[]> {

    private int n;
    private Integer[] arr;
    private AlgVisualizer algVisualizer;
    private ArrDisplay arrDisplay;
    private int numChunks;

    public ArrSorting(AlgVisualizer algVisualizer, Integer[] arr, ArrDisplay arrDisplay) {
        this.algVisualizer = algVisualizer;
        this.arr = arr;
        this.arrDisplay = arrDisplay;
        this.n = arr.length;
    }

    /*
	 * doInBackground() can only be called once by calling execute() This method
	 * determines what to do when an action is done. We use if statements to
	 * determine what buttons was clicked, helping determine what action to do next.
	 * If a reset was done, redraw the new shuffled array, and reset this object so
	 * that it is able to sort the next time execute() is called. If a sorting
	 * algorithm was clicked, simply perform that sorting algorithm. Make sure to
	 * set the start and end times for the performance window at the end of sorting.
     */
    @Override
    protected Void doInBackground() throws Exception {
        if (algVisualizer.stopSort()) {
            publish(arr.clone());
            sleep();
            algVisualizer.setSwingWorker(new ArrSorting(algVisualizer, arr, arrDisplay));
        } else {
            algVisualizer.setStartTime(System.currentTimeMillis());
            if (algVisualizer.getSort().equals("Bubble Sort")) {
                bubbleSort();
            } else if (algVisualizer.getSort().equals("Insertion Sort")) {
                insertionSort();
            } else if (algVisualizer.getSort().equals("Selection Sort")) {
                selectionSort();
            } else if (algVisualizer.getSort().equals("Merge Sort")) {
                mergeSort(0, arr.length - 1);
            } else if (algVisualizer.getSort().equals("Quick Sort")) {
                quickSort(0, arr.length - 1);
            }
        }
        return null;
    }

    /*
	 * This method accepts a clone of the array that was sent to it as a parameter
	 * from the sorting algorithms. If this method isn't finished executing by the
	 * time that another publish(Arr.clone()) is made, it will send in the array
	 * clone at the end of the list of chunks.
	 *
	 * Since the repaint() method can not keep up with the amount of chunks sent in
	 * from the sorting algorithms when there is very low delay time (Repaint time
	 * depends on the system), it will only draw a fraction of the array clones sent
	 * to it.
	 *
	 * To make sure that the colored bars representing the swapped indexes are in
	 * sync with the array being painted, we track the amount of chunks sent, and
	 * send it to arrDisplay, so that whenever repaint() has the opportunity to
	 * execute, the chunk number of the array clone that is being drawn will be
	 * represented by numChunk, which will be used to get the swappedIndexes from
	 * the list. ( numChunk are swappedIndexes.size() are always equal. )
     */
    @Override
    protected void process(List<Integer[]> chunks) {
        int chunksSize = chunks.size();
        for (int i = 0; i < chunksSize; i++) {
            // Paint each cloned array that was sent as a chunk
            arrDisplay.setNumChunk(numChunks++);
            arrDisplay.setArr(chunks.get(0));
            chunks.remove(0);
            arrDisplay.repaint();
        }
    }

    /*
	 * sleep()
	 *
	 * This method is called when a sorting algorithm is being visualized and the
	 * algorithm needs to sleep to slow down the visualization of sorting.
     */
    private void sleep() {
        try {
            Thread.sleep(algVisualizer.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
	 * All of the sorting algorithms below are constantly checking in the loops to
	 * see if sorting has been stopped by a reset. If sorting has been stopped, the
	 * if statement will be true and the sorting method will be terminated.
	 *
	 * After every index comparison, we add +1 to the total number of index
	 * comparisons.
	 *
	 * After every index swap, we add the pair of indexes swapped to the list in
	 * arrDisplay for them to be drawn. We then call publish() and send a clone of
	 * the array as a chunk to the process(List<> Chunks) method above, where it
	 * will be painted in the frame.
     */
    private void bubbleSort() {
        for (int i = 0; i < n - 1; i++) {
            if (algVisualizer.stopSort()) {
                break;
            }
            for (int j = 0; j < n - i - 1; j++) {
                if (algVisualizer.stopSort()) {
                    break;
                } else {
                    algVisualizer.setIndexComparisons(algVisualizer.getIndexComparisons() + 1);

                    if (arr[j] > arr[j + 1]) {
                        int temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;

                        arrDisplay.addSwappedIndexes(j, j + 1);
                        publish(arr.clone());
                        sleep();
                    }
                }
            }
        }
        // Sorting complete, loop done
        if (!algVisualizer.stopSort()) {
            arrDisplay.setComplete(true);
            publish(arr.clone());
            sleep();
        }
    }

    private void selectionSort() {
        for (int i = 0; i < n - 1; i++) {
            if (algVisualizer.stopSort()) {
                break;
            }
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                algVisualizer.setIndexComparisons(algVisualizer.getIndexComparisons() + 1);
                if (arr[j] < arr[min_idx]) {
                    min_idx = j;
                }
            }
            int temp = arr[min_idx];
            arr[min_idx] = arr[i];
            arr[i] = temp;

            arrDisplay.addSwappedIndexes(min_idx, i);
            publish(arr.clone());
            sleep();
        }
        if (!algVisualizer.stopSort()) {
            arrDisplay.setComplete(true);
            publish(arr.clone());
            sleep();
        }
    }

    private void insertionSort() {
        for (int i = 1; i < n; ++i) {
            if (algVisualizer.stopSort()) {
                break;
            }
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) { // compare arr[j] and arr[i]
                if (algVisualizer.stopSort()) {
                    break;
                }
                algVisualizer.setIndexComparisons(algVisualizer.getIndexComparisons() + 1);
                arr[j + 1] = arr[j];
                j = j - 1;
                arrDisplay.addSwappedIndexes(j, j + 1);
                publish(arr.clone());
                sleep();
            }

            algVisualizer.setIndexComparisons(algVisualizer.getIndexComparisons() + 1);
            arr[j + 1] = key;
        }
        if (!algVisualizer.stopSort()) {
            arrDisplay.setComplete(true);
            publish(arr.clone());
            sleep();
        }
    }

    private void mergeSort(int l, int r) {
        if (algVisualizer.getSort().equals("Merge Sort") && !algVisualizer.stopSort()) { // Needed because of recursion
            if (l < r) {
                int m = (l + r) / 2;
                mergeSort(l, m);
                mergeSort(m + 1, r);
                merge(l, m, r);
            }
            if (isSorted() && !algVisualizer.stopSort()) {
                arrDisplay.setComplete(true);
                publish(arr.clone());
                sleep();

            }
        }
    }

    private void merge(int l, int m, int r) {
        if (algVisualizer.getSort().equals("Merge Sort") && !algVisualizer.stopSort()) { // Needed because of recursion
            int n1 = m - l + 1;
            int n2 = r - m;
            int L[] = new int[n1];
            int R[] = new int[n2];

            for (int i = 0; i < n1; ++i) {
                L[i] = arr[l + i];
            }
            for (int j = 0; j < n2; ++j) {
                R[j] = arr[m + 1 + j];
            }
            int i = 0, j = 0;
            int k = l;
            while (i < n1 && j < n2) {

                if (algVisualizer.stopSort()) { // necessary to check this within every loop?
                    break;
                }
                algVisualizer.setIndexComparisons(algVisualizer.getIndexComparisons() + 1);

                if (L[i] <= R[j]) {
                    arr[k] = L[i];
                    i++;

                    arrDisplay.addSwappedIndexes(k, k + i);
                    publish(arr.clone());
                    sleep();

                } else {
                    arr[k] = R[j];
                    j++;

                    arrDisplay.addSwappedIndexes(k, k + j);
                    publish(arr.clone());
                    sleep();
                }
                k++;
            }

            while (i < n1) {
                if (algVisualizer.stopSort()) {
                    break;
                }
                arr[k] = L[i];

                arrDisplay.addSwappedIndexes(k, k + i);
                publish(arr.clone());
                sleep();

                i++;
                k++;
            }

            while (j < n2) {
                if (algVisualizer.stopSort()) {
                    break;
                }
                arr[k] = R[j];

                arrDisplay.addSwappedIndexes(k, k + j);
                publish(arr.clone());
                sleep();

                j++;
                k++;
            }
        }
    }

    private void quickSort(int low, int high) {
        if (algVisualizer.getSort().equals("Quick Sort") && !algVisualizer.stopSort()) { // Needed because of recursion
            if (low < high) {
                int pi = partition(low, high);
                quickSort(low, pi - 1);
                quickSort(pi + 1, high);
            }
            if (isSorted() && !algVisualizer.stopSort()) {
                arrDisplay.setComplete(true);
                publish(arr.clone());
                sleep();
            }
        }
    }

    // quicksort
    private int partition(int low, int high) {
        if (algVisualizer.getSort().equals("Quick Sort") && !algVisualizer.stopSort()) { // Needed because of recursion
            int pivot = arr[high];
            int i = (low - 1);

            for (int j = low; j < high; j++) {

                if (algVisualizer.getSort().equals("Quick Sort") && !algVisualizer.stopSort()) {
                    algVisualizer.setIndexComparisons(algVisualizer.getIndexComparisons() + 1);

                    if (arr[j] < pivot) {
                        i++;
                        int temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;

                        arrDisplay.addSwappedIndexes(i, j);
                        publish(arr.clone());
                        sleep();
                    }
                }
            }

            if (algVisualizer.getSort().equals("Quick Sort") && !algVisualizer.stopSort()) {
                // swap arr[i+1] and arr[high] (or pivot)
                int temp = arr[i + 1];
                arr[i + 1] = arr[high];
                arr[high] = temp;
                arrDisplay.addSwappedIndexes(i + 1, high);
                publish(arr.clone());
                sleep();
            }
            return i + 1;
        }
        return 0;
    }

    /*
	 * This method tells us if the array is sorted or not. Used to check in the
	 * recursive sorts.
     */
    private boolean isSorted() {
        boolean isSorted = true;

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                isSorted = false;
            }
        }
        return isSorted;
    }

}
