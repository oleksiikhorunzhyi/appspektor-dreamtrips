package com.worldventures.dreamtrips.modules.background_uploading.model;

public interface CompoundOperationModel<T> {
   int id();
   CompoundOperationState state();
   int progress();
   long millisLeft();
   int position();
   T body();
}
