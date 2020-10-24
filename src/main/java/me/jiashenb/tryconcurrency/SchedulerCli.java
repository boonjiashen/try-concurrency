package me.jiashenb.tryconcurrency;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerCli {

  private static final ScheduledExecutorService EXECUTOR_SERVICE =
      Executors.newSingleThreadScheduledExecutor();
  private static final Duration PERIOD = Duration.ofSeconds(1L);
  private static final Duration SCHEDULE_WINDOW = Duration.ofSeconds(5L);

  public static void main(String[] args) {
    EXECUTOR_SERVICE.schedule(
        () -> EXECUTOR_SERVICE.shutdownNow(), SCHEDULE_WINDOW.toMillis(), TimeUnit.MILLISECONDS);
    EXECUTOR_SERVICE.scheduleAtFixedRate(
        SchedulerCli::runTask, 0, PERIOD.toMillis(), TimeUnit.MILLISECONDS);
  }

  private static void runTask() {
    System.out.println("Current thread = " + Thread.currentThread().getName());
    System.out.println("Hello world!");
  }
}
