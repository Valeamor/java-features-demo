package com.sion.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class SocketClient extends SocketBase {
  private String host;
  private SocketChannel clientSocketChannel;

  private SocketClient(String host, int port) throws IOException {
    super(port);
    this.host = host;
  }

  public static <T extends Message> void runMyClient(Consumer<T> consumer, Class<T> clazz) {
    try {
      SocketClient client = new SocketClient("127.0.0.1", 8888);
      // push a message
      client.openChannel();
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put("{\"msg\":\"first message from client\"}".getBytes());
      buffer.flip();
      client.clientSocketChannel.write(buffer);
      buffer.clear();

      client.run(consumer, clazz);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public <T extends Message> void run(Consumer<T> consumer, Class<T> clazz) throws Exception {
    openChannel();
    while (clientSocketChannel != null) {
      read(consumer, clazz);
    }
  }

  private synchronized void openChannel() {
    if (clientSocketChannel == null) {
      try {
        clientSocketChannel = SocketChannel.open();
        clientSocketChannel.connect(new InetSocketAddress(host, port));
        clientSocketChannel.configureBlocking(false).register(selector, SelectionKey.OP_READ);
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (clientSocketChannel != null) {
      if (clientSocketChannel.isOpen()) {
        clientSocketChannel.close();
      }
      clientSocketChannel = null;
    }
  }
}
