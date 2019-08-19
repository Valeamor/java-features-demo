package com.sion.demo.java7;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Test;

public class TryWithResources {
  /**
   * any class implement
   *
   * @see java.lang.AutoCloseable
   * @see java.io.Closeable
   *
   * can use try-with-resources
   */
  @Test
  public void test0() throws Exception {
    try (BufferedReader br = new BufferedReader(new FileReader("file:path"))) {
      br.readLine();
    }
    //You can declare more than one resource to close:
    try (
        InputStream in = new FileInputStream("file:src");
        OutputStream out = new FileOutputStream("file:dest")) {
      // code
    }
  }
}
