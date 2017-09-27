package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.LifecycleEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.CommonModule;
import com.worldventures.dreamtrips.modules.picker.service.MediaPickerFacebookService;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseActivity extends InjectingActivity {

   @Inject protected AnalyticsInteractor analyticsInteractor;
   @Inject protected AuthInteractor authInteractor;
   @Inject protected PermissionDispatcher permissionDispatcher;
   @Inject protected MediaPickerFacebookService pickerFacebookService;

   private Subscription logoutSubscription;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      MonitoringHelper.setInteractionName(this);
      super.onCreate(savedInstanceState);
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONCREATE), Schedulers.immediate());
      subscribeToLogoutEvents();
   }

   private void subscribeToLogoutEvents() {
      logoutSubscription = authInteractor.logoutPipe().observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<LogoutCommand>()
               .onProgress((logoutCommand, integer) -> {
                  // If possible don't wait until command finishes completely
                  // as we don't show any blocking progress in UI currently
                  if (logoutCommand.isUserDataCleared()) {
                     unsubscribeFromLogoutEventsAndLaunchLogin();
                  }
               })
               .onSuccess(logoutCommand -> unsubscribeFromLogoutEventsAndLaunchLogin()));
   }

   private void unsubscribeFromLogoutEventsAndLaunchLogin() {
      unsubscribeFromLogoutEvents();
      openLoginActivity();
   }

   protected abstract void openLoginActivity();

   private void unsubscribeFromLogoutEvents() {
      if (logoutSubscription != null && !logoutSubscription.isUnsubscribed()) {
         logoutSubscription.unsubscribe();
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      unsubscribeFromLogoutEvents();
   }

   @Override
   protected void onStart() {
      analyticsInteractor.analyticsActionPipe().send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONSTART), Schedulers
            .immediate());
      super.onStart();
   }

   @Override
   protected void onStop() {
      super.onStop();
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONSTOP), Schedulers.immediate());
   }

   @Override
   protected void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONRESUME), Schedulers.immediate());
   }

   @Override
   protected void onPause() {
      super.onPause();
      analyticsInteractor.analyticsActionPipe().send(new LifecycleEvent(this, LifecycleEvent.ACTION_ONPAUSE), Schedulers
            .immediate());
   }

   @Override
   protected List<Object> getModules() {
      List<Object> modules = super.getModules();
      modules.add(new ActivityModule(this));
      modules.add(new CommonModule());
      return modules;
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (pickerFacebookService.onActivityResult(requestCode, resultCode, data)) return;
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      permissionDispatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }
}
