package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.dialog.TermsConditionsDialog;

import javax.inject.Inject;

import icepick.Icepick;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class ActivityWithPresenter<PM extends ActivityPresenter> extends BaseActivity implements ActivityPresenter.View {

   private PM presenter;
   private final PublishSubject<ActivityEvent> lifecycleSubject = PublishSubject.create();
   private boolean isPaused;
   @Inject OfflineWarningDelegate offlineWarningDelegate;

   public PM getPresentationModel() {
      return presenter;
   }

   abstract protected PM createPresentationModel(Bundle savedInstanceState);

   @Override
   protected void beforeCreateView(Bundle savedInstanceState) {
      this.presenter = createPresentationModel(savedInstanceState);
      inject(this.presenter);
      this.presenter.onInjected();
      Icepick.restoreInstanceState(this, savedInstanceState);
      this.presenter.restoreInstanceState(savedInstanceState);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      Icepick.saveInstanceState(this, outState);
      if (presenter != null) this.presenter.saveInstanceState(outState);
   }


   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      this.presenter.takeView(this);
   }

   @Override
   public boolean onPrepareOptionsMenu(Menu menu) {
      this.presenter.onMenuPrepared();
      return super.onPrepareOptionsMenu(menu);
   }

   public void informUser(String st) {
      Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void informUser(int stringId) {
      Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void showOfflineAlert() {
      offlineWarningDelegate.showOfflineWarning(this);
   }

   @Override
   public boolean isTabletLandscape() {
      return ViewUtils.isTablet(this) && ViewUtils.isLandscapeOrientation(this);
   }

   @Override
   public boolean isVisibleOnScreen() {
      return !isPaused;
   }

   @Override
   public void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {

   }

   @Override
   public void showTermsDialog() {
      TermsConditionsDialog.create().show(getSupportFragmentManager());
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getPresentationModel().onCreate(savedInstanceState);
      lifecycleSubject.onNext(ActivityEvent.CREATE);
   }

   @Override
   protected void onStart() {
      super.onStart();
      getPresentationModel().onStart();
      lifecycleSubject.onNext(ActivityEvent.START);
   }

   @Override
   protected void onResume() {
      isPaused = false;
      super.onResume();
      this.presenter.onResume();
      lifecycleSubject.onNext(ActivityEvent.RESUME);
   }

   @Override
   protected void onPause() {
      isPaused = true;
      lifecycleSubject.onNext(ActivityEvent.PAUSE);
      getPresentationModel().onPause();
      super.onPause();
   }

   @Override
   protected void onStop() {
      lifecycleSubject.onNext(ActivityEvent.STOP);
      super.onStop();
      getPresentationModel().onStop();
   }

   @Override
   public void onDestroy() {
      lifecycleSubject.onNext(ActivityEvent.DESTROY);
      if (getPresentationModel() != null) {
         getPresentationModel().dropView();
      }
      super.onDestroy();
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      presenter.onConfigurationChanged(newConfig);
   }

   @Override
   public <T> Observable<T> bind(Observable<T> observable) {
      return bindUntilDropView(observable);
   }

   @Override
   public <T> Observable<T> bindUntilStop(Observable<T> observable) {
      return observable.compose(RxLifecycle.bindUntilActivityEvent(lifecycleSubject, ActivityEvent.STOP));
   }

   @Override
   public <T> Observable<T> bindUntilDropView(Observable<T> observable) {
      return observable.compose(RxLifecycle.bindUntilActivityEvent(lifecycleSubject, ActivityEvent.DESTROY));
   }

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.compose(RxLifecycle.bindUntilActivityEvent(lifecycleSubject, ActivityEvent.DESTROY));
   }

   protected <T> Observable.Transformer<T, T> bindViewToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindView());
   }
}
