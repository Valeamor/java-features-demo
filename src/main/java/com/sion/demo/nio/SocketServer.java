package com.sion.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class SocketServer extends SocketBase {
  private ServerSocketChannel serverSocketChannel;

  private SocketServer(int port) throws IOException {
    super(port);
  }

  public static <T extends Message> void runMyServer(Consumer<T> consumer, Class<T> clazz) {
    try {
      SocketServer socketServer = new SocketServer(8888);
      socketServer.run(consumer, clazz);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public <T extends Message> void run(Consumer<T> consumer, Class<T> clazz) throws Exception {
    openChannel();
    // 单线程拉取消息并处理
    reading(consumer, clazz);
    accepting();
  }

  @Override
  public synchronized void close() throws IOException {
    if (serverSocketChannel != null) {
      if (serverSocketChannel.isOpen()) {
        serverSocketChannel.close();
      }
      serverSocketChannel = null;
    }
  }

  private synchronized void openChannel() {
    if (serverSocketChannel == null) {
      try {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  <T extends Message> void reading(Consumer<T> consumer, Class<T> clazz) {
    new Thread(() -> {
      while (serverSocketChannel != null) {
        try {
          read(consumer, clazz);
          Thread.sleep(10);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private synchronized void accepting() throws IOException {
    while (serverSocketChannel != null) {
      SocketChannel channel = serverSocketChannel.accept();
      channel.configureBlocking(false).register(selector, SelectionKey.OP_READ);
      System.out.println("connected: " + channel);
    }
  }
}
