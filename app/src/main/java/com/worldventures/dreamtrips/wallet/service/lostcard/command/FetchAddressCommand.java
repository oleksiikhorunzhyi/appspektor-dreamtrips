package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import android.location.Address;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchAddressCommand extends Command<Address> implements InjectableAction {

   @Inject WalletDetectLocationService locationService;
   private final double latitude;
   private final double longtitude;

   public FetchAddressCommand(double latitude, double longtitude) {
      this.latitude = latitude;
      this.longtitude = longtitude;
   }

   @Override
   protected void run(CommandCallback<Address> callback) throws Throwable {
      locationService.obtainAddressByGeoposition(latitude, longtitude)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}

