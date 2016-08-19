package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;

import com.messenger.storage.MessengerDatabase;
import com.messenger.synchmechanism.MessengerConnector;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.session.LogoutHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearMemoryStorageCommand;
import com.worldventures.dreamtrips.modules.common.service.ClearMemoryStoragesInteractor;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class LogoutDelegate {

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
   @Inject ClearMemoryStoragesInteractor clearMemoryStoragesInteractor;

   public LogoutDelegate(Injector injector) {
      injector.inject(this);
   }

   public void logout() {
      eventBus.post(new SessionHolder.Events.SessionDestroyed());
      messengerConnector.disconnect();
      authInteractor.unsubribeFromPushPipe()
            .createObservableResult(new UnsubribeFromPushCommand())
            .subscribe(action -> deleteSession(), throwable -> deleteSession());
   }

   private void deleteSession() {
      janet.createPipe(LogoutHttpAction.class, Schedulers.io())
            .createObservableResult(new LogoutHttpAction())
            .subscribe(action -> clearUserDataAndFinish(), throwable -> clearUserDataAndFinish());
   }

   private void clearUserDataAndFinish() {
      clearMemoryStoragesInteractor.clearMemoryStorageActionPipe().send(new ClearMemoryStorageCommand());
      cookieManager.clearCookies();
      snappyRepository.clearAll();
      appSessionHolder.destroy();
      notificationDelegate.cancelAll();
      badgeUpdater.updateBadge(0);
      FlowManager.getDatabase(MessengerDatabase.NAME).reset(context);
   }
}
