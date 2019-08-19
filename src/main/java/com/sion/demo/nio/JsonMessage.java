package com.sion.demo.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class JsonMessage extends Message {
  public JsonMessage(MessageDataPool dataPool, SocketChannel channel) {
    super(dataPool, channel);
  }

  private int _jc;
  private boolean _inQouta;

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Message> void consume(Consumer<T> consumer, ByteBuffer buffer) {
    if (cache == null) {
      cache = dataPool.poll();
      content = new StringBuilder();
    }
    while (buffer.hasRemaining()) {
      int remaining = Math.min(cache.remaining(), buffer.remaining());
      if (_c().length() + remaining >= 1024 * 1024) {
        close();
        throw new RuntimeException("cache overflow");
      }
      buffer.get(cache.data, cache.position, remaining);
      String newContent = new String(cache.data, cache.position, remaining);
      for (int i = 0, l = newContent.length(); i < l; i++) {
        String c = newContent.substring(i, i + 1);
        if (_inQouta) {
          if (c.equals("\"")) {
            _inQouta = false;
          }
        } else {
          switch (c) {
            case "\"":
              _inQouta = true;
              break;
            case "{":
              ++_jc;
              break;
            case "}":
              --_jc;
              break;
          }
        }
      }
      _c().append(newContent);
    }
    buffer.clear();
    if (_jc == 0 && _c().length() > 0) {
      content = _c().toString();
      consumer.accept((T) this);
      content = new StringBuilder();
    }
  }

  private StringBuilder _c() {
    return (StringBuilder) content;
  }
}
