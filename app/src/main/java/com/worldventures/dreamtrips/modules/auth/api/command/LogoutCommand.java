package com.worldventures.dreamtrips.modules.auth.api.command;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.messenger.storage.MessengerDatabase;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.util.CrashlyticsTracker;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.NxtSessionHolder;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.session.LogoutHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.auth.service.analytics.LogoutAction;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CancelAllCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.ClearStoragesInteractor;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import java.security.KeyStoreException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class LogoutCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject @ForApplication Context context;
   @Inject @Global EventBus eventBus;
   @Inject SnappyRepository snappyRepository;
   @Inject SessionHolder<UserSession> appSessionHolder;
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
   @Inject @Named(JanetModule.JANET_WALLET) SessionActionPipeCreator sessionWalletActionPipeCreator;
   @Inject HybridAndroidCrypter crypter;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject NxtSessionHolder nxtSessionHolder;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(clearSessionDependants(), args -> null)
            .flatMap(o -> clearSession())
            .flatMap(o -> clearUserData())
            .doOnError(throwable -> {
               Timber.w((Throwable) throwable, "Could not log out");
               CrashlyticsTracker.trackError((Throwable) throwable);
            })
            .subscribe(o -> logoutComplete(callback));
   }

   private void logoutComplete(CommandCallback<Void> callback) {
      analyticsInteractor.analyticsActionPipe().send(new LogoutAction());
      callback.onSuccess(null);
   }

   private Iterable<Observable<Void>> clearSessionDependants() {
      return Arrays.asList(clearWallet(), clearMessenger());
   }

   private Observable clearWallet() {
      return Observable.create(subscriber -> {
         sessionWalletActionPipeCreator.clearReplays();
         nxtSessionHolder.destroy();
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
         eventBus.post(new SessionHolder.Events.SessionDestroyed());
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

         try {
            crypter.deleteKeys();
         } catch (KeyStoreException e) {
            Timber.w(e, "Crypter keys are not cleared");
         }
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
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
