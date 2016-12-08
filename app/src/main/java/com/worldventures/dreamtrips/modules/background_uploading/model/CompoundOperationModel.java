package com.worldventures.dreamtrips.modules.background_uploading.model;

import java.util.Date;

public interface CompoundOperationModel<T> {
   int id();
   CompoundOperationState state();
   int progress();
   long millisLeft();
   Date creationDate();
   T body();
}
