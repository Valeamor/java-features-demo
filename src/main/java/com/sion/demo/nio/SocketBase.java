package com.sion.demo.nio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public abstract class SocketBase implements Closeable {
  final Selector selector;
  final int port;
  private final ByteBuffer buffer;
  private final MessageDataStackPool messageDataStackPool;

  SocketBase(int port) throws IOException {
    this.port = port;
    this.selector = Selector.open();
    this.buffer = ByteBuffer.allocate(1024 * 1024);
    this.messageDataStackPool = new MessageDataStackPool();
  }

  abstract <T extends Message> void run(Consumer<T> consumer, Class<T> clazz) throws Exception;

  <T extends Message> void read(Consumer<T> consumer, Class<T> clazz) throws Exception {
    int readReady = selector.selectNow();
    if (readReady > 0) {
      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();
        read(key, consumer, clazz);
      }
      selectedKeys.clear();
    }
  }

  @SuppressWarnings("unchecked")
  <T extends Message> void read(SelectionKey key, Consumer<T> consumer, Class<T> clazz) throws Exception {
    SocketChannel channel = (SocketChannel) key.channel();
    int bytesRead;
    while (true) {
      if ((bytesRead = channel.read(buffer)) <= 0) {
        break;
      }
    }
    buffer.flip();
    T message = (T) key.attachment();
    if (bytesRead == -1 || (message != null && !message.isOpen())) {
      if (message != null) {
        message.close();
        key.attach(null);
      }
      key.cancel();
      channel.close();
      System.out.println("disconnected");
    } else {
      if (message == null) {
        message = clazz.getConstructor(MessageDataPool.class, SocketChannel.class).newInstance(messageDataStackPool, channel);
        key.attach(message);
      }
      message.consume(consumer, buffer);
    }
  }
}
