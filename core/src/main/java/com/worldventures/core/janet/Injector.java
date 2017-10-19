package com.worldventures.core.janet;

import dagger.ObjectGraph;

public interface Injector {
   String OBJECT_GRAPH_SERVICE_NAME = "ObjectGraphKey";

   ObjectGraph getObjectGraph();

   void inject(Object target);
}
