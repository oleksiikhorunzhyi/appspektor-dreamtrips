package com.worldventures.dreamtrips.social.ui.background_uploading.model;

import org.immutables.value.Value;

import java.util.Date;

@Value.Immutable
public abstract class PostCompoundOperationModel<T extends PostBody> {

   public static final int TIME_LEFT_INITIAL_VALUE = -1;

   public abstract int id();

   public abstract CompoundOperationState state();

   @Value.Default
   public int progress() {
      return 0;
   }

   @Value.Default
   public long millisLeft() {
      return TIME_LEFT_INITIAL_VALUE;
   }

   @Value.Default
   public double averageUploadSpeed() {
      return 0.0d;
   }

   public abstract Date creationDate();

   public abstract PostBody.Type type();

   public abstract T body();

   @Override
   public boolean equals(Object obj) {
      return (obj instanceof PostCompoundOperationModel) && (id() == ((PostCompoundOperationModel) obj).id());
   }

   @Override
   public int hashCode() {
      return id();
   }
}
