package com.sion.demo.java7;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sion.demo.nio.JsonMessage;
import com.sion.demo.nio.SocketClient;
import com.sion.demo.nio.SocketServer;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class NewIOSocketServerAndClient {
  @Test
  public void runServer() {
    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
    AtomicInteger count = new AtomicInteger();

    SocketServer.runMyServer((message) -> {
      JSONObject content = JSON.parseObject((String) message.getContent());
      System.out.println("message from client: " + content.toJSONString());
      synchronized (buffer) {
        buffer.put(("{\"serverCount\":" + count.getAndIncrement() + "}").getBytes());
        buffer.flip();
        message.write(buffer);
        buffer.clear();
      }
    }, JsonMessage.class);
  }

  @Test
  public void runClient() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    AtomicInteger count = new AtomicInteger();

    SocketClient.runMyClient((message) -> {
      JSONObject content = JSON.parseObject((String) message.getContent());
      System.out.println("msg from server: " + content.toJSONString());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      synchronized (buffer) {
        buffer.put(("{\"clientCount\":" + count.getAndIncrement()).getBytes());
        buffer.flip();
        message.write(buffer);
        buffer.clear();
      }
      // 消息分成两部分发送
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      synchronized (buffer) {
        buffer.put(("}").getBytes());
        buffer.flip();
        message.write(buffer);
        buffer.clear();
      }
    }, JsonMessage.class);
  }
}
