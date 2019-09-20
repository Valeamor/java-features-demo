package com.sion.demo.designpatterns.behavioral.chainofresponsibility;

// another implementation with functional interface
@FunctionalInterface
public interface Handler2 {
  boolean handler(Object request);

  default Handler2 next(Handler2 next) {
    return (request) -> {
      boolean handled = handler(request);
      if (!handled) {
        handled = next.handler(request);
      }
      return handled;
    };
  }
}
