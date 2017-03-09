package com.worldventures.dreamtrips.modules.dtl.service;

import android.location.Location;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.NearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableLocationsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.composer.ActionPipeCancelWiper;
import com.worldventures.dreamtrips.modules.dtl.service.composer.ActionPipeClearWiper;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.schedulers.Schedulers;

public class DtlLocationInteractor implements Initializable {

   private final ActionPipe<LocationCommand> locationSourcePipe;
   private final ActionPipe<LocationFacadeCommand> locationFacadePipe;
   private final ActionPipe<NearbyLocationAction> nearbyLocationPipe;
   private final ActionPipe<SearchLocationAction> searchLocationPipe;

   public DtlLocationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {

      locationSourcePipe = sessionActionPipeCreator.createPipe(LocationCommand.class, Schedulers.io());
      locationFacadePipe = sessionActionPipeCreator.createPipe(LocationFacadeCommand.class, Schedulers.io());
      nearbyLocationPipe = sessionActionPipeCreator.createPipe(NearbyLocationAction.class, Schedulers.io());
      searchLocationPipe = sessionActionPipeCreator.createPipe(SearchLocationAction.class, Schedulers.io());

      connectLocationPipes();
      connectSearchCancelLatest();
      connectClearSearches();
      init();
   }

   public ReadActionPipe<LocationCommand> locationSourcePipe() {
      return locationSourcePipe;
   }

   public ReadActionPipe<LocationFacadeCommand> locationFacadePipe() {
      return locationFacadePipe;
   }

   public ReadActionPipe<NearbyLocationAction> nearbyLocationPipe() {
      return nearbyLocationPipe;
   }

   public ActionPipe<SearchLocationAction> searchLocationPipe() {
      return searchLocationPipe;
   }

   public void search(String query) {
      searchLocationPipe.cancelLatest();
      searchLocationPipe.send(SearchLocationAction.create(ImmutableLocationsActionParams.builder().query(query).build()));
   }

   public void clear() {
      locationSourcePipe.send(LocationCommand.clear());
   }

   public void changeSourceLocation(DtlLocation dtlLocation) {
      locationSourcePipe.send(LocationCommand.change(dtlLocation));
   }

   public void changeFacadeLocation(DtlLocation dtlLocation) {
      locationFacadePipe.send(LocationFacadeCommand.change(dtlLocation));
   }

   public void requestNearbyLocations(Location location) {
      nearbyLocationPipe.send(NearbyLocationAction.create(ImmutableLocationsActionParams.builder().location(location).build()));
   }

   private void connectSearchCancelLatest() {
      searchLocationPipe.observe().compose(new ActionPipeCancelWiper<>(nearbyLocationPipe)).subscribe();
   }

   private void connectClearSearches() {
      locationSourcePipe.observe().compose(new ActionPipeClearWiper<>(searchLocationPipe)).subscribe();
   }

   private void connectLocationPipes() {
      locationSourcePipe.observeSuccess()
            .map(LocationCommand::getResult)
            .map(LocationFacadeCommand::change)
            .subscribe(locationFacadePipe::send);
   }

   @Override
   public void init() {
      clear();
   }
}
