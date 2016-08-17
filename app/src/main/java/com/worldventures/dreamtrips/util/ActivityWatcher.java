package com.worldventures.dreamtrips.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ActivityWatcher implements Application.ActivityLifecycleCallbacks {
   private static final int TIMER_FOR_DISCONNECT = 2000;

   private final Handler handler = new Handler();
   private final List<OnStartStopAppListener> listeners = new CopyOnWriteArrayList<>();
   private final SessionHolder<UserSession> sessionHolder;

   private int visibleActivityCount;

   public ActivityWatcher(Context context, SessionHolder<UserSession> sessionHolder) {
      this.sessionHolder = sessionHolder;
      ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
   }

   public void addOnStartStopListener(OnStartStopAppListener listener) {
      listeners.add(listener);
   }

   public void removeOnStartStopListener(OnStartStopAppListener listener) {
      listeners.remove(listener);
   }

   @Override
   public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

   }

   private boolean isSessionExist() {
      Optional<UserSession> optionalSession = sessionHolder.get();
      UserSession session = optionalSession != null && optionalSession.isPresent() ? optionalSession.get() : null;
      return session != null && !TextUtils.isEmpty(session.getApiToken());
   }

   @Override
   public void onActivityStarted(Activity activity) {
      visibleActivityCount++;
      if (!isSessionExist()) return;
      if (visibleActivityCount != 1) return;

      handler.post(() -> {
         for (OnStartStopAppListener listener : listeners) {
            listener.onStartApplication();
         }
      });
   }

   @Override
   public void onActivityResumed(Activity activity) {

   }

   @Override
   public void onActivityPaused(Activity activity) {

   }

   @Override
   public void onActivityStopped(Activity activity) {
      visibleActivityCount--;

      handler.postDelayed(() -> {
         if (visibleActivityCount != 0) return;

         for (OnStartStopAppListener listener : listeners) {
            listener.onStopApplication();
         }
      }, TIMER_FOR_DISCONNECT);
   }

   @Override
   public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

   }

   @Override
   public void onActivityDestroyed(Activity activity) {
   }

   public interface OnStartStopAppListener {

      void onStartApplication();

      void onStopApplication();

   }
}
