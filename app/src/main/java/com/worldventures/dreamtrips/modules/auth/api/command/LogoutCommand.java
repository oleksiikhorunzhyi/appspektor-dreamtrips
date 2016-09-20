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
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
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

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LogoutCommand extends Command implements InjectableAction {

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
   @Inject @Named(JanetModule.JANET_API_LIB) SessionActionPipeCreator sessionApoActionPipeCreator;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      String apiToken = appSessionHolder.get().get().getApiToken();
      String pushToken = snappyRepository.getGcmRegToken();
      clearUserData();
      //
      messengerConnector.disconnect();
      authInteractor.unsubribeFromPushPipe().send(new UnsubribeFromPushCommand(apiToken, pushToken));
      logout(apiToken);
   }

   private void logout(String token) {
      LogoutHttpAction logoutHttpAction = new LogoutHttpAction();
      logoutHttpAction.setAuthorizationHeader(NewDreamTripsHttpService.getAuthorizationHeader(token));
      janet.createPipe(LogoutHttpAction.class).send(logoutHttpAction);
   }

   private void clearUserData() {
      appSessionHolder.destroy();
      eventBus.post(new SessionHolder.Events.SessionDestroyed());
      clearStoragesInteractor.clearMemoryStorageActionPipe().send(new ClearStoragesCommand());
      cookieManager.clearCookies();
      snappyRepository.clearAll();
      notificationDelegate.cancelAll();
      badgeUpdater.updateBadge(0);
      sessionActionPipeCreator.clearReplays();
      sessionApoActionPipeCreator.clearReplays();
      FlowManager.getDatabase(MessengerDatabase.NAME).reset(context);
      offlineWarningDelegate.resetState();
   }
}
