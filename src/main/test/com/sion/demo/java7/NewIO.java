package com.sion.demo.java7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.util.Objects;
import org.junit.Test;

public class NewIO {
  @Test
  public void oldIO() throws Exception {
    String path = Objects.requireNonNull(getClass().getClassLoader().getResource("testData.txt")).getPath();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      System.out.println("name: " + reader.readLine());
      System.out.println("age: " + reader.readLine());
      System.out.println("email: " + reader.readLine());
      System.out.println("phone: " + reader.readLine());
    }
  }

  /**
   * Here are the most important Channels implementations in Java NIO:
   *
   * @see java.nio.channels.FileChannel
   * @see java.nio.channels.DatagramChannel
   * @see java.nio.channels.SocketChannel
   * @see java.nio.channels.ServerSocketChannel
   *
   * The FileChannel reads data from and to files.
   * The DatagramChannel can read and write data over the network via UDP.
   * The SocketChannels can read and write data over the network via TCP.
   * The ServerSocketChannel allows you to listen for incoming TCP connections,
   * like a web server does. For each incoming connection a SocketChannels is created.
   *
   *
   * Using a Buffer to read and write data typically follows this little 4-step process:
   *
   * 1. Write data into the Buffer
   * 2. Call buffer.flip()
   * 3. Read data out of the Buffer
   * 4. Call buffer.clear() or buffer.compact()
   */

  @Test
  public void fileChannel() throws IOException {
    String path = Objects.requireNonNull(getClass().getClassLoader().getResource("testData.txt")).getPath();
    try (RandomAccessFile aFile = new RandomAccessFile(path, "r")) {
      FileChannel inChannel = aFile.getChannel();
      ByteBuffer buf = ByteBuffer.allocate(10);
      int bytesRead;
      while ((bytesRead = inChannel.read(buf)) != -1) { // step.1
        // You can write data into a Buffer in two ways:
        // 1. Write data from a Channels into a Buffer
        // 2. Write data into the Buffer yourself, via the buffer's put() methods.

        System.out.println("read:" + bytesRead);
        buf.flip(); // step.2 switch the buffer from writing mode into reading mode(position set to 0)

        System.out.print("\"");
        while (buf.hasRemaining()) {
          System.out.print((char) buf.get()); // step.3
        }
        System.out.print("\"");
        // There are two ways you can read data from a Buffer.
        // 1. Read data from the buffer into a channel.(channel.write(buf))
        // 2. Read data from the buffer yourself, using one of the get() methods.

        System.out.println();
        buf.clear(); // step.4 clear the buffer, to make it ready for writing again
        // The clear() method clears the whole buffer. (position set to 0)
        // The compact() method only clears the data which you have already read

        // You can mark a given position in a Buffer by calling the Buffer.mark() method.
        // You can then later reset the position back to the marked position by calling the Buffer.reset() method.
      }
    }
  }

  // A scattering read from a channel is a read operation that reads data into more than one buffer.
  // Thus, the channel "scatters" the data from the channel into multiple buffers.
  @Test
  public void scatterAndGather() throws IOException {
    String srcPath = Objects.requireNonNull(getClass().getClassLoader().getResource("testData.txt")).getPath();
    try (RandomAccessFile src = new RandomAccessFile(srcPath, "r");
        RandomAccessFile desc = new RandomAccessFile("scatterAndGather.test", "rw");
        FileChannel srcChannel = src.getChannel();
        FileChannel descChannel = desc.getChannel()) {// When you are done using a FileChannel you must close it.

      ByteBuffer part1 = ByteBuffer.allocate(8);
      ByteBuffer part2 = ByteBuffer.allocate(1024);

      ByteBuffer[] bufferArray = {part1, part2};


      srcChannel.read(bufferArray);
      // after read, srcChannel.position() will change

      part1.flip();
      part2.flip();

      descChannel.write(bufferArray);

      // The FileChannel.force() method flushes all unwritten data from the channel to the disk.
      // An operating system may cache data in memory for performance reasons,
      // so you are not guaranteed that data written to the channel is actually written to disk,
      // until you call the force() method.
      descChannel.force(false);

      // You can truncate a file by calling the FileChannel.truncate() method. When you truncate a file,
      // you cut it off at a given length. Here is an example:
      descChannel.truncate(10);
    }
  }

  @Test
  public void channelTransfer() throws IOException {
    String srcPath = Objects.requireNonNull(getClass().getClassLoader().getResource("testData.txt")).getPath();
    try (RandomAccessFile src = new RandomAccessFile(srcPath, "r");
        RandomAccessFile desc1 = new RandomAccessFile("channelTransfer1.test", "rw");
        RandomAccessFile desc2 = new RandomAccessFile("channelTransfer2.test", "rw");
        FileChannel srcChannel = src.getChannel();
        FileChannel descChannel1 = desc1.getChannel();
        FileChannel descChannel2 = desc2.getChannel()) {

      long count = srcChannel.size();

      descChannel1.transferFrom(srcChannel, 0, count);
      descChannel1.force(false);

      srcChannel.transferTo(0, count, descChannel2);
      descChannel2.force(false);
    }
  }

  @Test
  public void pipe() throws IOException {
    Pipe pipe = Pipe.open();
    Thread targetThread = new Thread(() -> {
      Pipe.SourceChannel sourceChannel = pipe.source();
      while (true) {
        ByteBuffer buf = ByteBuffer.allocate(48);
        try {
          int bytesRead = sourceChannel.read(buf);
          if (bytesRead > 0) {
            buf.flip();
            System.out.print("read data: ");
            while (buf.hasRemaining()) {
              System.out.print((char) buf.get());
            }
            buf.clear();
            System.out.println();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    targetThread.start();

    // send data to targetThread
    Pipe.SinkChannel sinkChannel = pipe.sink();
    while (true) {
      String newData = "time is " + System.currentTimeMillis();
      ByteBuffer buf = ByteBuffer.allocate(48);
      buf.clear();
      buf.put(newData.getBytes());
      buf.flip();
      while (buf.hasRemaining()) {
        try {
          sinkChannel.write(buf);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
