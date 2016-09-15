package com.worldventures.dreamtrips.core.api.request;

public abstract class Command<T> extends DreamTripsRequest<T> {
   public Command(Class<T> clazz) {
      super(clazz);
   }
}
