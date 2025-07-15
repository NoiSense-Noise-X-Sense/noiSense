package com.dosion.noisense.common.threadlocal;

public class TraceIdHolder {

  private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

  public static String get() {
    return threadLocal.get();
  }

  public static void set(String traceId) {
    threadLocal.set(traceId);
  }

  public static void clear() {
    threadLocal.remove();
  }
}
