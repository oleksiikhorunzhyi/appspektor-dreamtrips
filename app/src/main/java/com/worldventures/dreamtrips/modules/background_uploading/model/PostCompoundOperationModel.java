package com.worldventures.dreamtrips.modules.background_uploading.model;

import org.immutables.value.Value;

import java.util.Date;

@Value.Immutable
public interface PostCompoundOperationModel<T extends PostBody> {

   int id();
   CompoundOperationState state();
   int progress();
   long millisLeft();
   double averageUploadSpeed();
   Date creationDate();
   PostBody.Type type();
   T body();

}
