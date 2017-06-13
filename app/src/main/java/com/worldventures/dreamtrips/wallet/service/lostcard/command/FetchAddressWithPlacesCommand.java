package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.PlacesNearbyHttpAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

import static rx.Observable.zip;

@CommandAction
public class FetchAddressWithPlacesCommand extends Command<FetchAddressWithPlacesCommand.PlacesWithAddress>
      implements InjectableAction, CachedAction<Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>> {

   @Inject Janet janet;
   @Inject WalletDetectLocationService locationService;
   @Inject MapperyContext mapperyContext;

   private final WalletCoordinates coordinates;
   private PlacesWithAddress cachedResult;

   public FetchAddressWithPlacesCommand(WalletCoordinates coordinates) {
      this.coordinates = coordinates;
   }

   @Override
   protected void run(CommandCallback<FetchAddressWithPlacesCommand.PlacesWithAddress> callback) throws Throwable {
      if (!needApiRequest()) {
         callback.onSuccess(cachedResult);
         return;
      }
      zip(
            janet.createPipe(PlacesNearbyHttpAction.class)
                  .createObservableResult(new PlacesNearbyHttpAction(coordinates))
                  .map(httpAction -> mapperyContext.convert(httpAction.response().locationPlaces(), WalletPlace.class)),
            locationService.obtainAddressByGeoposition(coordinates.lat(), coordinates.lng())
                  .map(address -> mapperyContext.convert(address, WalletAddress.class)),
            (locationPlaces, address) -> new PlacesWithAddress(address, locationPlaces)
      )
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private boolean needApiRequest() {
      return cachedResult == null;
   }

   @Override
   public Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress> getCacheData() {
      return new Pair<>(coordinates, getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress> cache) {
      if (cache.first.equals(coordinates)) {
         cachedResult = cache.second;
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(needApiRequest())
            .build();
   }

   public WalletCoordinates getCoordinates() {
      return coordinates;
   }

   public class PlacesWithAddress {
      public final WalletAddress address;
      public final List<WalletPlace> places;

      private PlacesWithAddress(WalletAddress address, List<WalletPlace> places) {
         this.address = address;
         this.places = places;
      }
   }
}

