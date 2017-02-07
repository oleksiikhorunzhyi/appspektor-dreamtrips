package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.ImmutableWalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocationType;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;


public class WalletLocationCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardLocationInteractor locationInteractor;
   @Inject LostCardRepository locationRepository;
   private final WalletLocationType locationType;

   public WalletLocationCommand(WalletLocationType locationType) {
      this.locationType = locationType;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      final ImmutableWalletLocation.Builder locationBuilder = ImmutableWalletLocation.builder()
            .createdAt(Calendar.getInstance().getTime())
            .type(locationType);
      observeLocationDetection()
            .flatMap(geoLocationCommand -> appendCoordinates(locationBuilder, geoLocationCommand.getResult()))
            .flatMap(this::saveLocation)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<DetectGeoLocationCommand> observeLocationDetection() {
      return locationInteractor.detectGeoLocationPipe()
                  .createObservableResult(new DetectGeoLocationCommand());
   }

   private Observable<WalletLocation> appendCoordinates(ImmutableWalletLocation.Builder locationBuilder,
         WalletCoordinates walletCoordinates) {
      locationBuilder.coordinates(walletCoordinates);
      return Observable.just(locationBuilder.build());
   }

   private Observable<Void> saveLocation(WalletLocation location) {
      final List<WalletLocation> walletLocations = locationRepository.getWalletLocations();
      walletLocations.add(location);
      locationRepository.saveWalletLocations(walletLocations);
      return Observable.just(null);
   }


}
