package com.github.thorbenlindhauer;

public interface Listener<T> {

  void notify(String eventName, T object);
}
