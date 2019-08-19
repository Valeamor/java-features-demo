package com.sion.demo.nio;

public class ByteCache {
  final byte[] data;
  int position;

  ByteCache(int l) {
    this.data = new byte[l];
    this.position = 0;
  }

  int remaining() {
    return data.length - position;
  }
}
