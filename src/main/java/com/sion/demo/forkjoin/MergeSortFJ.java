package com.sion.demo.forkjoin;

import java.lang.reflect.Array;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSortFJ<T extends Comparable<T>> extends RecursiveAction {

  public static <T extends Comparable<T>> void sort(T[] arr) {
    MergeSortFJ<T> task = new MergeSortFJ<>(arr);
    new ForkJoinPool().invoke(task);
  }


  private final int start;
  private final int end;
  private final T[] arr;
  private final T[] cache;

  private static final int INSERTIONSORT_THRESHOLD = 7;

  @SuppressWarnings("unchecked")
  public MergeSortFJ(T[] arr) {
    this(arr, (T[]) Array.newInstance(arr[0].getClass(), arr.length), 0, arr.length);
  }

  private MergeSortFJ(T[] arr, T[] cache, int start, int end) {
    this.arr = arr;
    this.cache = cache;
    this.start = start;
    this.end = end;
  }

  @Override
  protected void compute() {
    if (end - start < INSERTIONSORT_THRESHOLD) {
      for (int i = start; i < end; i++) {
        for (int j = i; j > start && arr[j - 1].compareTo(arr[j]) > 0; j--) {
          T t = arr[j];
          arr[j] = arr[j - 1];
          arr[j - 1] = t;
        }
      }
    } else {
      int mid = (start + end) >>> 1;
      invokeAll(
          new MergeSortFJ<>(arr, cache, start, mid),
          new MergeSortFJ<>(arr, cache, mid, end)
      );
      merge(mid);
    }
  }

  private void merge(int mid) {
    System.arraycopy(arr, start, cache, start, end - start);
    int i = start;
    int j = mid;
    int k = start;
    while (i < mid && j < end) {
      if (cache[i].compareTo(cache[j]) <= 0) {
        arr[k] = cache[i];
        i++;
      } else {
        arr[k] = cache[j];
        j++;
      }
      k++;
    }
    while (i < mid) {
      arr[k] = cache[i];
      i++;
      k++;
    }
    while (j < end) {
      arr[k] = cache[j];
      j++;
      k++;
    }
  }
}
