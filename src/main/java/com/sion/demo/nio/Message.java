package com.sion.demo.nio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public abstract class Message implements Closeable {
  private final SocketChannel channel;
  private boolean isOpen;

  Object content;
  ByteCache cache;
  final MessageDataPool dataPool;

  Message(MessageDataPool dataPool, SocketChannel channel) {
    this.dataPool = dataPool;
    this.isOpen = true;
    this.channel = channel;
  }

  @Override
  public void close() {
    dataPool.add(cache);
    isOpen = false;
  }

  abstract <T extends Message> void consume(Consumer<T> consumer, ByteBuffer buffer);

  public boolean isOpen() {
    return isOpen;
  }

  public Object getContent() {
    return content;
  }

  public void write(ByteBuffer buffer) {
    while (buffer.hasRemaining()) {
      try {
        channel.write(buffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
