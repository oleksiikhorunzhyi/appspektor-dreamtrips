package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<SmartCard> implements InjectableAction, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private static final String DUMMY_DEVICE_NAME = "DUMMY_DEVICE_NAME"; // deviceName is not used inside SDK

   private SmartCardDetails smartCardDetails;
   private SmartCard smartCard;

   private String smartCardId;

   public CreateAndConnectToCardCommand(SmartCardDetails smartCardDetails) {
      this.smartCardDetails = smartCardDetails;
      this.smartCardId = String.valueOf(smartCardDetails.smartCardId());
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      Observable.just(createSmartCard())
            .flatMap(smartCard -> smartCardInteractor.connectActionPipe()
                  .createObservableResult(new ConnectSmartCardCommand(smartCard)))
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
            .serialNumber(smartCardDetails.serialNumber())
            .deviceAddress(smartCardDetails.bleAddress())
            .smartCardId(smartCardId)
            .cardStatus(SmartCard.CardStatus.DRAFT)
            .build();
   }

   public String getSmartCardId() {
      return smartCardId;
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
