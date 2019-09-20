package com.sion.demo.designpatterns;

import com.sion.demo.designpatterns.behavioral.chainofresponsibility.Handler;
import com.sion.demo.designpatterns.behavioral.chainofresponsibility.Handler2;
import org.junit.Test;

public class ChainOfResponsibility {

  @Test
  public void client() {
    Handler stringHandler = new Handler() {
      public void handler(Object request) {
        if (request instanceof String) {
          System.out.println("this is a string: " + request);
        } else if (next != null) {
          next.handler(request);
        }
      }
    };
    Handler integerHandler = new Handler() {
      public void handler(Object request) {
        if (request instanceof Integer) {
          System.out.println("this is a integer: " + request);
        } else if (next != null) {
          next.handler(request);
        }
      }
    };
    Handler commonHandler = new Handler() {
      public void handler(Object request) {
        System.out.println("this is a object: " + request);
      }
    };
    stringHandler.setNext(integerHandler);
    integerHandler.setNext(commonHandler);

    stringHandler.handler("hello, world");
    stringHandler.handler(10);
    stringHandler.handler(new Object());
  }

  // another implementation with functional interface
  @Test
  public void client2() {
    Handler2 handler2 = (Object request) -> {
      if (request instanceof String) {
        System.out.println("this is a string: " + request);
        return true;
      }
      return false;
    };
    handler2 = handler2.next(request -> {
      if (request instanceof Integer) {
        System.out.println("this is a integer: " + request);
        return true;
      }
      return false;
    }).next(request -> {
      if (request instanceof Boolean) {
        System.out.println("this is a boolean: " + request);
        return true;
      }
      return false;
    }).next(request -> {
      System.out.println("this is a object: " + request);
      return true;
    });

    handler2.handler("hello, world");
    handler2.handler(10);
    handler2.handler(true);
    handler2.handler(new Object());
  }
}
