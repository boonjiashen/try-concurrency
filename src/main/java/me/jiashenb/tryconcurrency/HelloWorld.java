package me.jiashenb.tryconcurrency;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class HelloWorld {

  public static void main(String[] args) {
    log.info("Hello world at thread = {}", Thread.currentThread().getName());
  }
}
