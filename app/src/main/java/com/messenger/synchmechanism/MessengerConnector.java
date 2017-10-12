package com.messenger.synchmechanism;

import android.content.Context;
import android.net.NetworkInfo;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.util.SessionHolderHelper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class MessengerConnector {

   private final BehaviorSubject<SyncStatus> connectionStream = BehaviorSubject.create(SyncStatus.DISCONNECTED);
   private final MessengerServerFacade messengerServerFacade;
   private final SessionHolder appSessionHolder;
   private final MessengerSyncDelegate messengerSyncDelegate;

   public MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher, SessionHolder appSessionHolder,
         MessengerServerFacade messengerServerFacade, MessengerSyncDelegate messengerSyncDelegate) {
      this.appSessionHolder = appSessionHolder;
      this.messengerServerFacade = messengerServerFacade;
      this.messengerSyncDelegate = messengerSyncDelegate;
      ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribe(connectivity -> onEvent(connectivity.getState()));

      messengerServerFacade.getStatusObservable().subscribe(this::handleConnectionStatus);

      registerActivityWatcher(activityWatcher);
   }

   private void registerActivityWatcher(ActivityWatcher activityWatcher) {
      ActivityWatcher.OnStartStopAppListener startStopAppListener = new ActivityWatcher.OnStartStopAppListener() {
         @Override
         public void onStartApplication() {
            connect();
         }

         @Override
         public void onStopApplication() {
            disconnect();
         }
      };
      activityWatcher.addOnStartStopListener(startStopAppListener);
   }

   private void handleConnectionStatus(ConnectionStatus status) {
      switch (status) {
         case DISCONNECTED:
            connectionStream.onNext(SyncStatus.DISCONNECTED);
            break;
         case CONNECTING:
            connectionStream.onNext(SyncStatus.CONNECTING);
            break;
         case ERROR:
            connectionStream.onNext(SyncStatus.ERROR);
            break;
         case CONNECTED:
            syncData();
            break;
         default:
      }
   }

   public Observable<SyncStatus> status() {
      return connectionStream.asObservable();
   }

   public Observable<ConnectionStatus> getAuthToServerStatus() {
      return messengerServerFacade.getStatusObservable();
   }

   public void connect() {
      if (messengerServerFacade.isConnected() || !SessionHolderHelper.hasEntity(appSessionHolder)) return;
      UserSession userSession = appSessionHolder.get().get();
      if (userSession.getUser() == null) return;
      messengerServerFacade.connect(userSession.getUsername(), userSession.getLegacyApiToken());
   }

   public void disconnect() {
      messengerServerFacade.disconnect();
   }

   private void onEvent(NetworkInfo.State status) {
      boolean internetConnected = status == NetworkInfo.State.CONNECTED;
      if (internetConnected) {
         connect();
      } else {
         disconnect();
      }
   }

   private void syncData() {
      if (messengerServerFacade.sendInitialPresence()) {
         connectionStream.onNext(SyncStatus.SYNC_DATA);
         messengerSyncDelegate.sync().subscribe(result -> {
            Timber.d("Sync succeed");
            messengerServerFacade.setActive(result);
            connectionStream.onNext(SyncStatus.CONNECTED);
         }, e -> {
            disconnect();
            Timber.e(e, "Sync failed");
            connectionStream.onNext(SyncStatus.ERROR);
         });
      } else {
         Timber.e("Sync failed");
         connectionStream.onNext(SyncStatus.ERROR);
      }
   }
}
