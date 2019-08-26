package com.sion.demo.forkjoin;

import java.lang.reflect.Array;

public class MergeSort {
  private static final int INSERTIONSORT_THRESHOLD = 7;

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<T>> void sort(T[] arr) {
    T[] cache = (T[]) Array.newInstance(arr[0].getClass(), arr.length);
    sort(arr, cache, 0, arr.length);
  }

  private static <T extends Comparable<T>> void sort(T[] arr, T[] cache, int start, int end) {
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
      sort(arr, cache, start, mid);
      sort(arr, cache, mid, end);
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
}