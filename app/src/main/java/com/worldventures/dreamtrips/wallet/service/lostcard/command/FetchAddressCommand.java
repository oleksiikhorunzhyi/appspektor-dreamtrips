package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class FetchAddressCommand extends Command<WalletAddress> implements InjectableAction {

   @Inject WalletDetectLocationService locationService;
   @Inject MapperyContext mapperyContext;
   private final WalletCoordinates walletCoordinates;

   public FetchAddressCommand(WalletCoordinates walletCoordinates) {
      this.walletCoordinates = walletCoordinates;
   }

   @Override
   protected void run(CommandCallback<WalletAddress> callback) throws Throwable {
      locationService.obtainAddressByGeoposition(walletCoordinates.lat(), walletCoordinates.lng())
            .map(address ->  mapperyContext.convert(address, WalletAddress.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}

