package com.worldventures.janet.injection;

public interface ActionServiceLogger {
   void error(Throwable throwable, String message, Object...args);
}
