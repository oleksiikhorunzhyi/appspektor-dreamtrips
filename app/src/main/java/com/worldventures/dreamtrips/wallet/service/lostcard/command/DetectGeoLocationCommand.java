package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocationType;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.SCLocationRepository;
import com.worldventures.dreamtrips.wallet.util.LocationPermissionDeniedException;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DetectGeoLocationCommand extends Command<Void> implements InjectableAction {

   @Inject WalletDetectLocationService locationService;
   @Inject SCLocationRepository locationRepository;
   private final WalletLocationType type;

   public DetectGeoLocationCommand(WalletLocationType type) {
      this.type = type;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (locationService.isPermissionGranted()) {
         throw new LocationPermissionDeniedException();
      }
      locationService.detectLastKnownLocation()
            .map(location -> constructWalletLocation(type, location.getLatitude(), location.getLongitude()))
            .subscribe((result) -> {
               saveLocation(result);
               callback.onSuccess(null);
            }, callback::onFail);
   }

   private void saveLocation(WalletLocation location) {
      final List<WalletLocation> walletLocations = locationRepository.getWalletLocations();
      walletLocations.add(location);
      locationRepository.saveWalletLocations(walletLocations);
   }

   private WalletLocation constructWalletLocation(WalletLocationType type, double latitude, double longtitude) {
      final WalletCoordinates coordinates = ImmutableWalletCoordinates.builder()
            .lat(latitude)
            .lng(longtitude)
            .build();
      return ImmutableWalletLocation.builder()
            .coordinates(coordinates)
            .createdAt(Calendar.getInstance().getTime())
            .type(type)
            .build();
   }
}
