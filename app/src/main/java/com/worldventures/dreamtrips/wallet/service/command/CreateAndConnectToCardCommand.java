package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<SmartCard> implements InjectableAction, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject WizardMemoryStorage wizardMemoryStorage;
   @Inject SystemPropertiesProvider propertiesProvider;

   private final boolean waitForParing;
   private final boolean stayAwake;

   public CreateAndConnectToCardCommand() {
      this(true, true);
   }

   public CreateAndConnectToCardCommand(boolean waitForParing, boolean stayAwake) {
      this.waitForParing = waitForParing;
      this.stayAwake = stayAwake;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      janet.createPipe(ConnectSmartCardCommand.class)
            .createObservableResult(new ConnectSmartCardCommand(createSmartCard(), waitForParing, stayAwake))
            .map(ConnectSmartCardCommand::getResult)
            .subscribe(smartCard -> {
               if (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED) {
                  callback.onSuccess(smartCard);
               } else {
                  callback.onFail(new SmartCardConnectException("Could not connect to the device"));
               }
            }, callback::onFail);
   }

   private SmartCard createSmartCard() {
      return ImmutableSmartCard.builder()
            .smartCardId(String.valueOf(Long.valueOf(wizardMemoryStorage.getBarcode()))) //remove zeros from start
            .cardStatus(SmartCard.CardStatus.DRAFT)
            .deviceId(propertiesProvider.deviceId())
            .build();
   }

   @Override
   public SmartCard getCacheData() {
      return getResult();
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
