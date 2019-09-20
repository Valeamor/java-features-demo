package com.sion.demo.designpatterns.behavioral.chainofresponsibility;

public abstract class Handler {
  protected Handler next;

  public abstract void handler(Object request);

  public void setNext(Handler next) {
    this.next = next;
  }
}