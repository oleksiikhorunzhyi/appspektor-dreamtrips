package com.worldventures.dreamtrips;

import com.worldventures.core.di.BaseApplicationWithInjector;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.core.module.AppModule;

import dagger.ObjectGraph;

public class App extends BaseApplicationWithInjector {

   @Override
   protected Injector createInjector() {
      return new Injector() {
         private ObjectGraph objectGraph;

         @Override
         public ObjectGraph getObjectGraph() {
            if (objectGraph == null) {
               objectGraph = ObjectGraph.create(new AppModule(App.this));
            }
            return objectGraph;
         }

         @Override
         public void inject(Object target) {
            getObjectGraph().inject(target);
         }
      };
   }
}
