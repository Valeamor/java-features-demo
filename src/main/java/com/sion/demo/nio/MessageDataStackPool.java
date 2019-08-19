package com.sion.demo.nio;

import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageDataStackPool extends MessageDataPool {
  private final ConcurrentLinkedDeque<ByteCache> pool;
  private final int bufferSize;

  public MessageDataStackPool() {
    // default 128k * 100
    this(128, 10);
  }

  public MessageDataStackPool(int bufferSize, int initAmount) {
    this.bufferSize = bufferSize;
    pool = new ConcurrentLinkedDeque<>();
    for (int i = 0; i < initAmount; i++) {
      add(newCache());
    }
  }

  @Override
  public ByteCache poll() {
    ByteCache cache = pool.poll();
    return cache == null ? newCache() : cache;
  }

  @Override
  public ByteCache newCache() {
    return new ByteCache(bufferSize);
  }

  @Override
  void add(ByteCache cache) {
    pool.add(cache);
  }

  @Override
  public int size() {
    return pool.size();
  }
}
