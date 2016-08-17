package com.techery.spares.module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class ConcurentModule {
   @Provides
   ExecutorService provideExecutorService() {
      return Executors.newSingleThreadExecutor();
   }
}
