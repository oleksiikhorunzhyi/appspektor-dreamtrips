package com.worldventures.dreamtrips.wallet.service;

import com.innahema.collections.query.queriables.Queryable;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;
import io.techery.janet.smartcard.exception.SmartCardRequestException;
import io.techery.janet.smartcard.exception.WaitingResponseException;

public class SmartCardErrorServiceWrapper extends ActionServiceWrapper {

   private final List<SmartCardRequestListener> listeners = new ArrayList<>();

   public SmartCardErrorServiceWrapper(ActionService actionService) {
      super(actionService);
   }

   public void addRequestFailureListener(SmartCardRequestListener listener) {
      listeners.add(listener);
   }

   public void removeRequestFailureListener(SmartCardRequestListener listener) {
      listeners.remove(listener);
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      return false;
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof SmartCardRequestException) {
         notifyListeners(cause, ((SmartCardRequestException) cause).getRequestName());
      } else if (cause instanceof WaitingResponseException) {
         notifyListeners(cause, cause.getMessage());
      }
      return false;
   }

   private void notifyListeners(Throwable t, String message) {
      Queryable.from(listeners).notNulls().forEachR(listener -> listener.onFailure(t, message));
   }

   public interface SmartCardRequestListener {
      void onFailure(Throwable error, String message);
   }

}
