package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import android.location.Location;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;
import com.worldventures.dreamtrips.wallet.util.LocationPermissionDeniedException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DetectGeoLocationCommand extends Command<WalletCoordinates> implements InjectableAction {

   @Inject WalletDetectLocationService locationService;
   @Inject LostCardRepository locationRepository;

   @Override
   protected void run(CommandCallback<WalletCoordinates> callback) throws Throwable {
      if (locationService.isPermissionGranted()) {
         throw new LocationPermissionDeniedException();
      }
      locationService.detectLastKnownLocation()
            .map(this::constructCoordinates)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private WalletCoordinates constructCoordinates(Location location) {
      return ImmutableWalletCoordinates.builder()
            .lat(location.getLatitude())
            .lng(location.getLongitude())
            .build();

   }
}
