package com.sion.demo.java7;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import org.junit.Test;

public class NewIOFileAPI {

  @Test
  public void testPaths() {
    Path path = Paths.get("/Users/sion");
    System.out.println("Number of Nodes:" + path.getNameCount());
    System.out.println("File Name:" + path.getFileName());
    System.out.println("File Root:" + path.getRoot());
    System.out.println("File Parent:" + path.getParent());

  }

  @Test
  public void testFiles() throws IOException {
    Path path = Paths.get("/Users/sion/testfile");
    Path path2 = Paths.get("/Users/sion/testfile2");

    Files.createFile(path);
    Files.copy(path, path2);
    Files.delete(path);
    Files.move(path2, path);
    Files.deleteIfExists(path);

    Files.createFile(path);
    Files.createLink(path2, path);
    Files.delete(path);
    Files.createSymbolicLink(path, path2);
    Files.delete(path);
    Files.delete(path2);
  }

  @Test
  public void testWatchService() throws IOException, InterruptedException {
    WatchService watchService = FileSystems.getDefault().newWatchService();
    Path path = Paths.get("/Users/sion");
    path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
    while (true) {
      WatchKey key = watchService.take(); // this would return you keys
      for (WatchEvent<?> event : key.pollEvents()) {
        System.out.println("Event on " + event.context().toString() + " is " + event.kind());
      }
      if (!key.reset()) {
        System.out.println("key is no longer not valid");
        break;
      }
    }
  }
}
