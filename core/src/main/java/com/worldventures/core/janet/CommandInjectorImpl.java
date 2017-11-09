package com.worldventures.core.janet;


import com.worldventures.janet.injection.CommandInjector;

import dagger.ObjectGraph;
import timber.log.Timber;

public class CommandInjectorImpl implements CommandInjector {

   private final ObjectGraph objectGraph;

   public CommandInjectorImpl(ObjectGraph objectGraph) {
      this.objectGraph = objectGraph;
   }

   @Override
   public void inject(Object action) {
      try {
         objectGraph.inject(action);
      } catch (Throwable e) {
         String detailMessage = "No graph method found to inject " + action.getClass()
               .getSimpleName() + ". Check your component";
         Timber.e(e, detailMessage);
         throw e;
      }

   }
}
