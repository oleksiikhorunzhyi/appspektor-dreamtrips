package com.worldventures.wallet.service;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.service.analytics.AnalyticsService;
import com.worldventures.janet.analytics.AnalyticsEvent;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;
import timber.log.Timber;

public class WalletAnalyticsServiceWrapper extends ActionServiceWrapper {

   private final List<NavigationStateListener> listeners = new ArrayList<>();

   public WalletAnalyticsServiceWrapper(ActionService analyticsService) {
      super(analyticsService);
   }

   public void addNavigationStateListener(NavigationStateListener listener) {
      listeners.add(listener);
   }

   public void removeNavigationStateListener(NavigationStateListener listener) {
      listeners.remove(listener);
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      if (isNavigationState(holder)) {
         try {
            notifyListeners(AnalyticsService.getAction(holder));
         } catch (IllegalAccessException e) {
            Timber.e(e);
         }
      }

      return false;
   }

   private boolean isNavigationState(ActionHolder holder) {
      return holder.action().getClass().getAnnotation(AnalyticsEvent.class).navigationState();
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      return false;
   }

   private void notifyListeners(String state) {
      Queryable.from(listeners).notNulls().forEachR(listener -> listener.onNewNavigationState(state));
   }

   public interface NavigationStateListener {
      void onNewNavigationState(String state);
   }

}
