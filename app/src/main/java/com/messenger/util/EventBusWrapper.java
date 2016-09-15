package com.messenger.util;

import com.github.pwittchen.networkevents.library.BusWrapper;

import de.greenrobot.event.EventBus;

public class EventBusWrapper implements BusWrapper {

   private EventBus bus;

   public EventBusWrapper(EventBus bus) {
      this.bus = bus;
   }

   @Override
   public void register(Object object) {
      bus.register(object);
   }

   @Override
   public void unregister(Object object) {
      bus.unregister(object);
   }

   @Override
   public void post(Object event) {
      bus.post(event);
   }
}
