package com.techery.spares.module;

import dagger.ObjectGraph;

public interface Injector {
   ObjectGraph getObjectGraph();

   void inject(Object target);
}
