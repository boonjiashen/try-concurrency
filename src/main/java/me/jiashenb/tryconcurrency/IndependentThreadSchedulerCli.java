package me.jiashenb.tryconcurrency;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

/** Runs each scheduled task on a separate thread so that the tasks can be long running */
@Log4j2
public class IndependentThreadSchedulerCli {

  private static final ScheduledExecutorService TERMINATOR_SERVICE =
      Executors.newSingleThreadScheduledExecutor();
  private static final ScheduledExecutorService PERIODIC_SERVICE =
      Executors.newSingleThreadScheduledExecutor();
  private static final ExecutorService WORKER_SERVICE = Executors.newCachedThreadPool();

  private static final Duration PERIOD = Duration.ofMillis(200L);
  private static final Duration TEST_WINDOW = Duration.ofSeconds(1L);
  private static final Duration TASK_DURATION = Duration.ofMillis(5000L);

  private static final Set<Integer> TASK_IDS = Collections.newSetFromMap(new ConcurrentHashMap<>());

  private static final Random RANDOM = new Random();

  public static void main(String[] args) {
    run();
  }

  private static void run() {
    PERIODIC_SERVICE.schedule(
        (Runnable) PERIODIC_SERVICE::shutdownNow, TEST_WINDOW.toMillis(), TimeUnit.MILLISECONDS);
    PERIODIC_SERVICE.scheduleAtFixedRate(
        () -> WORKER_SERVICE.execute(IndependentThreadSchedulerCli::runTask),
        0,
        PERIOD.toMillis(),
        TimeUnit.MILLISECONDS);
    blockTillTermination(PERIODIC_SERVICE);
    cleanup();
  }

  @SneakyThrows
  private static void blockTillTermination(ExecutorService service) {
    service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
  }

  @SneakyThrows
  private static void cleanup() {
    WORKER_SERVICE.shutdown();
    blockTillTermination(WORKER_SERVICE);
    log.info("Ran {} tasks", TASK_IDS.size());
  }

  private static void runTask() {
    int taskId = Math.abs(RANDOM.nextInt());
    log.info(
        "Starting taskId = {}, sleep = {}, thread-id = {}",
        taskId,
        TASK_DURATION,
        Thread.currentThread().getName());
    sleep(TASK_DURATION);
    TASK_IDS.add(taskId);
    log.info("Ending   taskId = {}", taskId);
  }

  @SneakyThrows
  private static void sleep(Duration duration) {
    Thread.sleep(duration.toMillis());
  }
}
