package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardCoordinates;
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
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
   private final SmartCardLocationType type;

   public DetectGeoLocationCommand(SmartCardLocationType type) {
      this.type = type;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (locationService.isPermissionGranted()) {
         throw new LocationPermissionDeniedException();
      }
      locationService.detectLastKnownLocation()
            .map(location -> constructSmartCardLocation(type, location.getLatitude(), location.getLongitude()))
            .subscribe((result) -> {
               saveLocation(result);
               callback.onSuccess(null);
            }, callback::onFail);
   }

   private void saveLocation(SmartCardLocation location) {
      final List<SmartCardLocation> smartCardLocations = locationRepository.getSmartCardLocations();
      smartCardLocations.add(location);
      locationRepository.saveSmartCardLocations(smartCardLocations);
   }

   private SmartCardLocation constructSmartCardLocation(SmartCardLocationType type, double latitude, double longtitude) {
      final SmartCardCoordinates coordinates = ImmutableSmartCardCoordinates.builder()
            .lat(latitude)
            .lng(longtitude)
            .build();
      return ImmutableSmartCardLocation.builder()
            .coordinates(coordinates)
            .createdAt(Calendar.getInstance().getTime())
            .type(type)
            .build();
   }
}
