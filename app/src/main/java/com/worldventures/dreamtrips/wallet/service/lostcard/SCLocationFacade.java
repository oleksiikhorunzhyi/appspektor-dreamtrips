package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.CreateLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;

import io.techery.janet.ActionPipe;

public class SCLocationFacade {

   private final SmartCardInteractor smartCardInteractor;
   private final SCLocationRepository locationRepository;

   public SCLocationFacade(SmartCardInteractor interactor, SCLocationRepository repository) {
      this.smartCardInteractor = interactor;
      this.locationRepository = repository;
   }

   public ActionPipe<GetLocationCommand> prepareFetchLocationPipe() {
      return smartCardInteractor.getLocationPipe();
   }

   public void fetchLocation() {
      prepareFetchLocationPipe().send(new GetLocationCommand());
   }

   public void saveLocationToStorage(SmartCardLocation smartCardLocation) {
      locationRepository.saveSmartCardLocation(smartCardLocation);
   }

   public ActionPipe<CreateLocationCommand> prepareCreateLocationPipe() {
      return smartCardInteractor.createLocationPipe();
   }

   public void saveLocationToServer(boolean isNetworkAvailable) {
      if (isNetworkAvailable) {
         smartCardInteractor.createLocationPipe().send(new CreateLocationCommand());
      } else {
         //TODO : Implement JobScheduler and send command to it
      }
   }
}
