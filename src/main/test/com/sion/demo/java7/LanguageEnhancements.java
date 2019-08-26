package com.sion.demo.java7;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;

public class LanguageEnhancements {
  @Test
  public void diamondOperator() {
    Map<String, List<String>> strings = new TreeMap<String, List<String>>(); // before 7
    Map<String, List<String>> strings2 = new TreeMap<>(); // Now
  }

  @Test
  public void usingStringInSwitchStatements() {
    switch ("a") {
      case "a":
        break;
      case "b":
        break;
    }
  }

  /**
   * any class implement
   *
   * @see java.lang.AutoCloseable
   * @see java.io.Closeable
   *
   * can use try-with-resources
   */
  @Test
  public void tryWithResources() throws Exception {
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

  @Test
  public void numericLiteralsWithUnderscores() {
    int thousand = 1_000;
    int million = 1_000_000;
  }

  @Test
  public void improvedExceptionHandling() {
    try {
      InputStream in = new FileInputStream("file:src");
    } catch (IOException e) {
    } catch (IllegalArgumentException e) {
    } catch (Exception e) {
    }
    try {
      InputStream in = new FileInputStream("file:src");
    } catch (IOException | IllegalArgumentException e) {
    } catch (Exception e) {
    }
  }
}
