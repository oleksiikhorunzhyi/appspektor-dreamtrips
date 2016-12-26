package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.Context;
import android.os.Bundle;

import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.util.JanetHttpErrorHandlingUtils;

import java.io.IOException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import io.techery.janet.CancelException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

public class Presenter<VT extends Presenter.View> {

   protected VT view;

   @Inject protected Context context;
   @Inject protected ActivityRouter activityRouter;
   @Inject @Global protected EventBus eventBus;
   @Inject protected SessionHolder<UserSession> appSessionHolder;
   @Inject protected AnalyticsInteractor analyticsInteractor;
   @Inject protected FeatureManager featureManager;
   @Inject protected PhotoUploadingManagerS3 photoUploadingManagerS3;
   @Inject OfflineWarningDelegate offlineWarningDelegate;
   @Inject protected OfflineErrorInteractor offlineErrorInteractor;

   protected int priorityEventBus = 0;

   protected ApiErrorPresenter apiErrorPresenter;

   private PublishSubject<Void> destroyViewStopper = PublishSubject.create();
   private PublishSubject<Void> pauseViewStopper = PublishSubject.create();

   protected PublishSubject<ConnectionState> connectionStatePublishSubject = PublishSubject.create();

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
      initConnectionOverlay();
   }

   protected void initConnectionOverlay() {
      view.initConnectionOverlay(Observable.merge(connectionStatePublishSubject,
            getConnectivityObservable()
                  .filter(connectivity -> connectivity != null)
                  .map(connectivity -> {
                     switch (connectivity.getState()) {
                        case CONNECTED:
                           return ConnectionState.CONNECTED;
                        default:
                           return ConnectionState.DISCONNECTED;
                     }
                  })), destroyViewStopper);
   }

   public void dropView() {
      destroyViewStopper.onNext(null);
      this.view = null;
      apiErrorPresenter.dropView();
      if (eventBus.isRegistered(this)) eventBus.unregister(this);
   }

   public void onStart() {
   }

   public void onResume() {
      //nothing to do here
      if (canShowOfflineAlert()) {
         subscribeToConnectivityStateUpdates();
      }
   }

   public void onPause() {
      pauseViewStopper.onNext(null);
   }

   public void onStop() {
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

   protected <T> Observable.Transformer<T, T> bindUntilPauseIoToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindUntilPause());
   }

   protected <T> Observable.Transformer<T, T> bindViewToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindView());
   }

   public boolean isConnected() {
      return Utils.isConnected(context);
   }

   public void handleError(Throwable error) {
      if (handleGenericError(error)) return;
      view.informUser(R.string.smth_went_wrong);
   }

   public void handleError(Object action, Throwable error) {
      if (handleGenericError(error)) return;
      if (action instanceof CommandWithError) {
         view.informUser(((CommandWithError) action).getErrorMessage());
      }
      String message = JanetHttpErrorHandlingUtils.handleJanetHttpError(context,
            action, error, context.getString(R.string.smth_went_wrong));
      view.informUser(message);
   }

   private boolean handleGenericError(Throwable error) {
      if (getCauseByType(CancelException.class, error) != null) return true;
      if (getCauseByType(IOException.class, error) != null) {
         reportNoConnectionWithOfflineErrorPipe(error);
         return true;
      }
      return false;
   }

   public void reportNoConnectionWithOfflineErrorPipe() {
      reportNoConnectionWithOfflineErrorPipe(new IOException());
   }

   private void reportNoConnectionWithOfflineErrorPipe(Throwable throwable) {
      offlineErrorInteractor.offlineErrorCommandPipe().send(new OfflineErrorCommand(throwable));
      reportNoConnection();
   }

   public void reportNoConnection() {
      connectionStatePublishSubject.onNext(ConnectionState.DISCONNECTED);
   }

   private void subscribeToConnectivityStateUpdates() {
      // since there is no replay functionality in the lib make delegate check it straight away
      getConnectivityObservable()
            .compose(bindUntilPause())
            .subscribe(connectivity -> {
               if (view.isVisibleOnScreen() && offlineWarningDelegate.needToShowOfflineAlert(context)) {
                  view.showOfflineAlert();
               }
            }, e -> Timber.e(e, "Could not subscribe to network events"));
   }

   private Observable<Connectivity> getConnectivityObservable() {
      return Observable.merge(Observable.just(null), ReactiveNetwork.observeNetworkConnectivity(context))
            .observeOn(AndroidSchedulers.mainThread());
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

   public interface View extends TabletAnalytic {

      void informUser(int stringId);

      void informUser(String string);

      boolean isVisibleOnScreen();

      void showOfflineAlert();

      void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper);
   }

   public interface TabletAnalytic {

      boolean isTabletLandscape();
   }
}
