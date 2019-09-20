package com.sion.demo.designpatterns.behavioral.observer;

public abstract class Observer {
  Subject subject;

  abstract void update(Object... messages);
}
