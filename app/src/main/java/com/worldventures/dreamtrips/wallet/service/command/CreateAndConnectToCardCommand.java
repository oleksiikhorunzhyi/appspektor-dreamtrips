package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<SmartCard> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SystemPropertiesProvider propertiesProvider;
   @Inject SmartCardInteractor interactor;

   private final boolean waitForParing;
   private final String barcode;

   public CreateAndConnectToCardCommand(String barcode) {
      this(barcode, true);
   }

   public CreateAndConnectToCardCommand(String barcode, boolean waitForParing) {
      this.barcode = barcode;
      this.waitForParing = waitForParing;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      final String smartCardId = String.valueOf(Long.valueOf(barcode)); //remove zeros from start

      janet.createPipe(ConnectSmartCardCommand.class)
            .createObservableResult(new ConnectSmartCardCommand(smartCardId, waitForParing))
            .flatMap(command -> interactor.activeSmartCardPipe()
                  .createObservableResult(new ActiveSmartCardCommand(createSmartCard(smartCardId))))
            .subscribe(command -> callback.onSuccess(command.getResult()), callback::onFail);
   }

   private SmartCard createSmartCard(String scId) {
      return ImmutableSmartCard.builder()
            .smartCardId(scId)
            .cardStatus(SmartCard.CardStatus.IN_PROVISIONING)
            .deviceId(propertiesProvider.deviceId())
            .build();
   }
}
