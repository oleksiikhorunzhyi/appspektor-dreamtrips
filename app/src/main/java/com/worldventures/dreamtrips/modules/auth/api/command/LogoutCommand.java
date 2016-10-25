package com.worldventures.dreamtrips.modules.auth.api.command;

import android.content.Context;

import com.messenger.storage.MessengerDatabase;
import com.messenger.synchmechanism.MessengerConnector;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.session.LogoutHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.ClearStoragesInteractor;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateActiveCardUserCommand;

import java.security.KeyStoreException;

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

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject @ForApplication Context context;
   @Inject @Global EventBus eventBus;
   @Inject SnappyRepository snappyRepository;
   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject NotificationDelegate notificationDelegate;
   @Inject BadgeUpdater badgeUpdater;
   @Inject DTCookieManager cookieManager;
   @Inject AuthInteractor authInteractor;
   @Inject MessengerConnector messengerConnector;
   @Inject OfflineWarningDelegate offlineWarningDelegate;
   @Inject ClearStoragesInteractor clearStoragesInteractor;
   @Inject SessionActionPipeCreator sessionActionPipeCreator;
   @Inject @Named(JanetModule.JANET_API_LIB) SessionActionPipeCreator sessionApiActionPipeCreator;
   @Inject @Named(JanetModule.JANET_WALLET) SessionActionPipeCreator sessionWalletActionPipeCreator;
   @Inject HybridAndroidCrypter crypter;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(clearWallet(), clearMessenger(), (o1, o2) -> null)
            .flatMap(o -> clearSession())
            .flatMap(o -> clearUserData())
            .subscribe();
      callback.onSuccess(null);
   }

   private Observable clearWallet() {
      return smartCardInteractor.disassociateActiveCardActionPipe()
            .createObservableResult(new DisassociateActiveCardUserCommand())
            .onErrorResumeNext(Observable.empty())
            .doOnCompleted(() -> sessionWalletActionPipeCreator.clearReplays());
   }

   private Observable clearMessenger() {
      return Observable.create(subscriber -> {
         messengerConnector.disconnect();
         try {
            FlowManager.getDatabase(MessengerDatabase.NAME).reset(context);
         } catch (Exception e) {
            Timber.w(e, "Messenger DB is not cleared");
         }
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

   private Observable clearSession() {
      return janet.createPipe(LogoutHttpAction.class).createObservableResult(new LogoutHttpAction())
            .onErrorResumeNext(n -> null)
            .flatMap(o -> {
               String apiToken = appSessionHolder.get().get().getApiToken();
               String pushToken = snappyRepository.getGcmRegToken();
               return authInteractor.unsubribeFromPushPipe()
                     .createObservableResult(new UnsubribeFromPushCommand(apiToken, pushToken))
                     .onErrorResumeNext(Observable.empty());
            })
            .doOnCompleted(() -> {
               cookieManager.clearCookies();
               appSessionHolder.destroy();
               eventBus.post(new SessionHolder.Events.SessionDestroyed());
               sessionActionPipeCreator.clearReplays();
               sessionApiActionPipeCreator.clearReplays();
            });
   }

   private Observable clearUserData() {
      return Observable.create(subscriber -> {
         clearStoragesInteractor.clearMemoryStorageActionPipe().send(new ClearStoragesCommand());
         notificationDelegate.cancelAll();
         badgeUpdater.updateBadge(0);
         offlineWarningDelegate.resetState();
         snappyRepository.clearAll();

         try {
            crypter.deleteKeys();
         } catch (KeyStoreException e) {
            Timber.w(e, "Crypter keys are not cleared");
         }
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }
}
