package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.util.JanetHttpErrorHandlingUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import io.techery.janet.CancelException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class Presenter<VT extends Presenter.View> implements RequestingPresenter, DreamSpiceManager.FailureListener {

   protected VT view;

   @Inject protected Context context;
   @Inject protected ActivityRouter activityRouter;
   @Inject @Global protected EventBus eventBus;
   @Inject protected SessionHolder<UserSession> appSessionHolder;
   @Inject protected AnalyticsInteractor analyticsInteractor;
   @Inject protected FeatureManager featureManager;
   @Inject protected DreamSpiceManager dreamSpiceManager;
   @Inject protected PhotoUploadingManagerS3 photoUploadingManagerS3;
   @Inject OfflineWarningDelegate offlineWarningDelegate;

   private Subscription connectivityEventsSubscription;

   protected int priorityEventBus = 0;

   protected ApiErrorPresenter apiErrorPresenter;

   PublishSubject<Void> destroyViewStopper = PublishSubject.create();
   PublishSubject<Void> pauseViewStopper = PublishSubject.create();

   public Presenter() {
      apiErrorPresenter = provideApiErrorPresenter();
   }

   protected ApiErrorPresenter provideApiErrorPresenter() {
      return new ApiErrorPresenter();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Lifecycle
   ///////////////////////////////////////////////////////////////////////////

   public void onInjected() {
      // safe hook to use injected members
   }

   public void restoreInstanceState(Bundle savedState) {
      Icepick.restoreInstanceState(this, savedState);
   }

   public void saveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public void takeView(VT view) {
      this.view = view;
      try {
         eventBus.registerSticky(this, priorityEventBus);
      } catch (Exception ignored) {
         Timber.v("EventBus :: Problem on registering sticky - no \'onEvent' method found in " + getClass().getName());
      }
   }

   public void dropView() {
      destroyViewStopper.onNext(null);
      this.view = null;
      apiErrorPresenter.dropView();
      if (eventBus.isRegistered(this)) eventBus.unregister(this);
   }

   public void onStart() {
      startSpiceManagers();
   }

   public void onResume() {
      //nothing to do here
      if (canShowOfflineAlert()) {
         subscribeToConnectivityStateUpdates();
      }
   }

   public void onPause() {
      pauseViewStopper.onNext(null);
      if (connectivityEventsSubscription != null && !connectivityEventsSubscription.isUnsubscribed()) {
         connectivityEventsSubscription.unsubscribe();
      }
   }

   public void onStop() {
      stopSpiceManagers();
   }

   public void onMenuPrepared() {
      // hook for onPreparedMenu
   }

   public void onCreate(Bundle savedInstanceState) {

   }

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.takeUntil(destroyViewStopper);
   }

   protected <T> Observable.Transformer<T, T> bindUntilPause() {
      return input -> input.takeUntil(pauseViewStopper);
   }

   protected <T> Observable.Transformer<T, T> bindViewToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindView());
   }

   ///////////////////////////////////////////////////////////////////////////
   // Spice manager
   ///////////////////////////////////////////////////////////////////////////

   private void startSpiceManagers() {
      if (!dreamSpiceManager.isStarted()) {
         dreamSpiceManager.start(context);
      }
   }

   private void stopSpiceManagers() {
      if (dreamSpiceManager.isStarted()) {
         dreamSpiceManager.shouldStop();
      }
   }

   @Override
   public <T> void doRequest(SpiceRequest<T> request) {
      dreamSpiceManager.execute(request, r -> {
      }, this);
   }

   @Override
   public <T> void doRequest(SpiceRequest<T> request, DreamSpiceManager.SuccessListener<T> successListener) {
      dreamSpiceManager.execute(request, successListener, this);
   }

   @Override
   @Deprecated
   public <T> void doRequest(SpiceRequest<T> request, DreamSpiceManager.SuccessListener<T> successListener, DreamSpiceManager.FailureListener failureListener) {
      dreamSpiceManager.execute(request, successListener, failureListener);
   }

   public boolean isConnected() {
      return Utils.isConnected(context);
   }

   @Deprecated
   @Override
   public void handleError(SpiceException error) {
      if (apiErrorPresenter.hasView()) {
         apiErrorPresenter.handleError(error);
      } else if (error != null && !TextUtils.isEmpty(error.getMessage())) {
         if (!error.getMessage().contains("cancelled")) { //hotfix, as robospice doesn't mark spice exception
            view.informUser(error.getMessage());
         }
      } else {
         view.informUser(R.string.smth_went_wrong);
      }
   }

   /**
    * @deprecated use CommandWithError and Presenter.handleError(action, throwable)
    */
   protected void handleError(Throwable throwable) {
      if (apiErrorPresenter.hasView()) {
         apiErrorPresenter.handleError(throwable);
      } else {
         Timber.d("ApiErrorPresenter has detached view");
         view.informUser(R.string.smth_went_wrong);
      }
   }

   protected void handleError(Object action, Throwable error) {
      if (error instanceof CancelException) return;
      if (action instanceof CommandWithError) {
         view.informUser(((CommandWithError) action).getErrorMessage());
         return;
      }
      String message = JanetHttpErrorHandlingUtils.handleJanetHttpError(context,
            action, error, context.getString(R.string.smth_went_wrong));
      view.informUser(message);
   }
   private void subscribeToConnectivityStateUpdates() {
      // since there is no replay functionality in the lib make delegate check it straight away
      connectivityEventsSubscription = Observable.merge(Observable.just(null), ReactiveNetwork.observeNetworkConnectivity(context))
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(connectivity -> {
               if (view.isVisibleOnScreen() && offlineWarningDelegate.needToShowOfflineAlert(context)) {
                  view.showOfflineAlert();
               }
            }, e -> Timber.e(e, "Could not subscribe to network events"));
   }

   protected boolean canShowOfflineAlert() {
      return true;
   }

   ///////////////////////////////////////////////////////////////////////////
   // User helpers
   ///////////////////////////////////////////////////////////////////////////

   public User getAccount() {
      return appSessionHolder.get().get().getUser();
   }

   public String getAccountUserId() {
      return getAccount().getUsername();
   }

   ///////////////////////////////////////////////////////////////////////////
   // UI helpers
   ///////////////////////////////////////////////////////////////////////////

   public interface View extends TabletAnalytic {

      void informUser(int stringId);

      void informUser(String string);

      void alert(String s);

      boolean isVisibleOnScreen();

      void showOfflineAlert();
   }

   public interface TabletAnalytic {

      boolean isTabletLandscape();
   }
}
