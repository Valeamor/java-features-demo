package com.sion.demo.java7;

import com.sion.demo.forkjoin.MergeSort;
import com.sion.demo.forkjoin.MergeSortFJ;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

public class ForkJoinFramework {

  // 多线程与单线程执行对比
  @Test
  public void mergeSort() {
    Integer[] arr = new Integer[1000000];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = i;
    }
    shuffleArray(arr);
    long t = System.currentTimeMillis();
    MergeSortFJ.sort(arr);
    System.out.print("multi thread cost:");
    System.out.println(System.currentTimeMillis() - t);


    shuffleArray(arr);
    t = System.currentTimeMillis();
    MergeSort.sort(arr);
    System.out.print("single thread cost:");
    System.out.println(System.currentTimeMillis() - t);
  }

  private static void shuffleArray(Integer[] ar) {
    Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      int a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }
}
