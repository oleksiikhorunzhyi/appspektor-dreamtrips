package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.SetClearRecordsDelayAction;
import rx.Observable;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<SmartCard> implements InjectableAction, CachedAction<SmartCard> {

   private final static long DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS = 2 * 60 * 24;

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
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
            .map(ConnectSmartCardCommand::getResult)
            .flatMap(this::setDefaults)
            .doOnNext(result -> this.smartCard = result)
            .subscribe(connectCommand -> {
               if (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED) {
                  callback.onSuccess(smartCard);
               } else {
                  callback.onFail(new SmartCardConnectException("Could not connect to the device"));
               }
            }, callback::onFail);
   }

   private Observable<SmartCard> setDefaults(SmartCard smartCard) {
      return janet.createPipe(SetClearRecordsDelayAction.class)
            .createObservableResult(new SetClearRecordsDelayAction(TimeUnit.MINUTES, DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS))
            .map(action -> ImmutableSmartCard.copyOf(smartCard)
                  .withClearFlyeDelay(DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS));
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
