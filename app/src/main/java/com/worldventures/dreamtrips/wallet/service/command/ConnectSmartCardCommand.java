package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class ConnectSmartCardCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   private SmartCard activeSmartCard;

   public ConnectSmartCardCommand(SmartCard activeSmartCard) {
      this.activeSmartCard = activeSmartCard;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      janet.createPipe(ConnectAction.class)
            .createObservableResult(new ConnectAction(ImmutableConnectionParams.of((int) Long.parseLong(activeSmartCard.smartCardId()))))
            .flatMap(action -> fetchTechnicalProperties())
            .doOnNext(smartCard -> activeSmartCard = smartCard)
            .subscribe(smartCard -> setStatusAndNotifyCallback(SmartCard.ConnectionStatus.CONNECTED, callback),
                  throwable -> {
                     Timber.e(throwable, "Error while connecting to smart card");
                     setStatusAndNotifyCallback(SmartCard.ConnectionStatus.ERROR, callback);
                  });
   }

   private void setStatusAndNotifyCallback(SmartCard.ConnectionStatus connectionStatus, CommandCallback<SmartCard> callback) {
      activeSmartCard = ImmutableSmartCard.copyOf(activeSmartCard).withConnectionStatus(connectionStatus);
      callback.onSuccess(activeSmartCard);
   }

   private Observable<SmartCard> fetchTechnicalProperties() {
      return janet.createPipe(FetchCardPropertiesCommand.class)
            .createObservableResult(new FetchCardPropertiesCommand(activeSmartCard))
            .map(Command::getResult);
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
