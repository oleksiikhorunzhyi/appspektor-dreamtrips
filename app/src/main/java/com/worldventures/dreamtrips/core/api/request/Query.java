package com.worldventures.dreamtrips.core.api.request;

public abstract class Query<T> extends DreamTripsRequest<T> {
   public Query(Class<T> clazz) {
      super(clazz);
   }
}
