package com.worldventures.dreamtrips.modules.auth.api.command;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.messenger.storage.MessengerDatabase;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.util.CrashlyticsTracker;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.session.LogoutHttpAction;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CancelAllCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.ClearStoragesInteractor;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class LogoutCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject @ForApplication Context context;
   @Inject SnappyRepository snappyRepository;
   @Inject SessionHolder appSessionHolder;
   @Inject LocaleSwitcher localeSwitcher;
   @Inject NotificationDelegate notificationDelegate;
   @Inject BadgeUpdater badgeUpdater;
   @Inject DTCookieManager cookieManager;
   @Inject AuthInteractor authInteractor;
   @Inject MessengerConnector messengerConnector;
   @Inject OfflineWarningDelegate offlineWarningDelegate;
   @Inject ReplayEventDelegatesWiper replayEventDelegatesWiper;
   @Inject ClearStoragesInteractor clearStoragesInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject SessionActionPipeCreator sessionActionPipeCreator;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Inject Set<LogoutAction> logoutActions;

   private boolean userDataCleared;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(clearSessionDependants(), args -> null)
            .flatMap(o -> clearSession().doOnNext(o1 -> {
               userDataCleared = true;
               callback.onProgress(0);
            }))
            .flatMap(o -> clearUserData())
            .doOnError(throwable -> {
               Timber.w((Throwable) throwable, "Could not log out");
               CrashlyticsTracker.trackError((Throwable) throwable);
            })
            .subscribe(o -> logoutComplete(callback));
   }

   private void logoutComplete(CommandCallback<Void> callback) {
      analyticsInteractor.analyticsActionPipe().send(new com.worldventures.dreamtrips.modules.auth.service.analytics.LogoutAction());
      callback.onSuccess(null);
   }

   private Iterable<Observable<Void>> clearSessionDependants() {
      return Arrays.asList(executeLogoutActions(), clearMessenger());
   }

   private Observable executeLogoutActions() {
      return Observable.create(subscriber -> {
         for (LogoutAction action : logoutActions) {
            try {
               action.call();
            } catch (Exception e) {
               Timber.e(e, "%s was failed", action);
            }
         }
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   private Observable clearMessenger() {
      return Observable.create(subscriber -> {
         messengerConnector.disconnect();
         try {
            FlowManager.getDatabase(MessengerDatabase.NAME).reset(context);
         } catch (Throwable e) {
            Timber.w(e, "Messenger DB is not cleared");
         }
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   private Observable clearSession() {
      String apiToken = appSessionHolder.get().get().getApiToken();
      String pushToken = snappyRepository.getGcmRegToken();
      //
      return Observable.create(subscriber -> {
         cookieManager.clearCookies();
         appSessionHolder.destroy();
         localeSwitcher.resetLocale();
         sessionActionPipeCreator.clearReplays();
         //
         subscriber.onNext(null);
         subscriber.onCompleted();
      }).flatMap(o -> {
         return authInteractor.unsubribeFromPushPipe()
               .createObservableResult(new UnsubribeFromPushCommand(apiToken, pushToken))
               .onErrorResumeNext(Observable.just(null));
      }).flatMap(o -> {
         return janet.createPipe(LogoutHttpAction.class)
               .createObservableResult(authorize(new LogoutHttpAction(), apiToken))
               .onErrorResumeNext(Observable.just(null));
      });
   }

   private Observable clearUserData() {
      return Observable.create(subscriber -> {
         backgroundUploadingInteractor.cancelAllCompoundOperationsPipe().send(new CancelAllCompoundOperationsCommand());
         clearStoragesInteractor.clearMemoryStorageActionPipe().send(new ClearStoragesCommand());
         notificationDelegate.cancelAll();
         badgeUpdater.updateBadge(0);
         offlineWarningDelegate.resetState();
         replayEventDelegatesWiper.clearReplays();
         snappyRepository.clearAll();
         clearFrescoCaches();

         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   public boolean isUserDataCleared() {
      return userDataCleared;
   }

   private void clearFrescoCaches() {
      ImagePipeline imagePipeline = Fresco.getImagePipeline();
      imagePipeline.clearCaches();
   }

   static <T extends AuthorizedHttpAction> T authorize(T action, String token) {
      action.setAuthorizationHeader(NewDreamTripsHttpService.getAuthorizationHeader(token));
      return action;
   }
}
