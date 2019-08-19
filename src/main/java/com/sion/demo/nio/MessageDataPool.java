package com.sion.demo.nio;

import java.util.Iterator;

public abstract class MessageDataPool {
  public abstract ByteCache poll();

  public abstract ByteCache newCache();

  public abstract int size();

  abstract void add(ByteCache cache);

  void putBack(Iterable<ByteCache> cache) {
    if (cache != null) {
      Iterator<ByteCache> iterator = cache.iterator();
      while (iterator.hasNext()) {
        add(iterator.next());
        iterator.remove();
      }
    }
  }
}
