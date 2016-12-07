package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<SmartCard> implements InjectableAction, CachedAction<SmartCard> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WizardMemoryStorage wizardMemoryStorage;

   private static final String DUMMY_DEVICE_NAME = "DUMMY_DEVICE_NAME"; // deviceName is not used inside SDK

   private SmartCard smartCard;

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      Observable.just(createSmartCard())
            .flatMap(smartCard -> smartCardInteractor.connectActionPipe()
                  .createObservableResult(new ConnectSmartCardCommand(smartCard, true, true))
            )
            .doOnNext(command -> this.smartCard = command.getResult())
            .subscribe(connectCommand -> {
               if (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED) {
                  callback.onSuccess(smartCard);
               } else {
                  callback.onFail(new SmartCardConnectException("Could not connect to the device"));
               }
            }, callback::onFail);
   }

   private SmartCard createSmartCard() {
      return ImmutableSmartCard.builder()
            .deviceName(DUMMY_DEVICE_NAME)
            .smartCardId(String.valueOf(Long.valueOf(wizardMemoryStorage.getBarcode()))) //remove zeros from start
            .cardStatus(SmartCard.CardStatus.DRAFT)
            .build();
   }

   @Override
   public SmartCard getCacheData() {
      return smartCard;
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
