package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ConnectionType;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.domain.entity.SmartCard.ConnectionStatus.CONNECTED;

@CommandAction
public class ConnectSmartCardCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   private SmartCard activeSmartCard;
   private final boolean waitForParing;
   private final boolean stayAwake;

   public ConnectSmartCardCommand(SmartCard activeSmartCard, boolean waitForParing) {
      this(activeSmartCard, waitForParing, false);
   }

   public ConnectSmartCardCommand(SmartCard activeSmartCard, boolean waitForParing, boolean stayAwake) {
      this.activeSmartCard = activeSmartCard;
      this.waitForParing = waitForParing;
      this.stayAwake = stayAwake;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      Observable<Object> observable = janet.createPipe(ConnectAction.class)
            .createObservableResult(new ConnectAction(ImmutableConnectionParams.of((int) Long.parseLong(activeSmartCard.smartCardId()))))
            .doOnNext(action -> {
               SmartCard.ConnectionStatus status = CONNECTED;
               if (action.type == ConnectionType.DFU) {
                  status = SmartCard.ConnectionStatus.DFU;
               }
               activeSmartCard = smartCardWithStatus(status);
            })
            .flatMap(action -> {
               if (stayAwake) {
                  return janet.createPipe(EnableLockUnlockDeviceAction.class)
                        .createObservableResult(new EnableLockUnlockDeviceAction(false))
                        .onErrorResumeNext(Observable.just(null));
               } else {
                  return Observable.just(action);
               }
            });

      if (waitForParing) {
         observable = observable.delay(20L, TimeUnit.SECONDS); //TODO: Hard code for waiting typing PIN
      }

      observable.flatMap(action -> {
         if (activeSmartCard.connectionStatus() == CONNECTED) return fetchTechnicalProperties();
         else return Observable.just(activeSmartCard);
      })
            .doOnNext(smartCard -> activeSmartCard = smartCard)
            .subscribe(smartCard -> {
                     callback.onSuccess(activeSmartCard);
                  },
                  throwable -> {
                     Timber.e(throwable, "Error while connecting to smart card");
                     callback.onSuccess(smartCardWithStatus(SmartCard.ConnectionStatus.ERROR));
                  }
            );
   }

   private SmartCard smartCardWithStatus(SmartCard.ConnectionStatus connectionStatus) {
      return ImmutableSmartCard.copyOf(activeSmartCard).withConnectionStatus(connectionStatus);
   }

   private Observable<SmartCard> fetchTechnicalProperties() {
      return janet.createPipe(FetchCardPropertiesCommand.class)
            .createObservableResult(new FetchCardPropertiesCommand())
            .map(Command::getResult)
            .map(properties -> ImmutableSmartCard.builder().from(activeSmartCard)
                  .sdkVersion(properties.sdkVersion())
                  .firmWareVersion(properties.firmWareVersion())
                  .batteryLevel(properties.batteryLevel())
                  .lock(properties.lock())
                  .stealthMode(properties.stealthMode())
                  .disableCardDelay(properties.disableCardDelay())
                  .clearFlyeDelay(properties.clearFlyeDelay())
                  .build());
   }

   @Override
   public SmartCard getCacheData() {
      return activeSmartCard;
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }
}
