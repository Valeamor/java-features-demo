package com.sion.demo.designpatterns.behavioral.observer;

import java.util.LinkedList;
import java.util.List;

public abstract class Subject {
  /**
   * 用来保存注册的观察者对象
   */
  private List<Observer> list = new LinkedList<>();

  public void register(Observer observer) {
    list.add(observer);
    observer.subject = this;
  }

  public void remove(Observer observer) {
    list.remove(observer);
  }

  public void nodify() {
    for (Observer observer : list) {
      observer.update(message());
    }
  }

  abstract Object[] message();
}
