package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.service.http.action.PlacesNearbyHttpAction;
import com.worldventures.dreamtrips.wallet.service.lostcard.model.LocationPlace;
import com.worldventures.dreamtrips.wallet.service.lostcard.model.NearbyResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchPlacesNearbyCommand extends Command<List<LocationPlace>> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;

   private final WalletCoordinates location;

   public FetchPlacesNearbyCommand(WalletCoordinates location) {
      this.location = location;
   }
   @Override
   protected void run(CommandCallback<List<LocationPlace>> callback) throws Throwable {
      janet.createPipe(PlacesNearbyHttpAction.class)
            .createObservableResult(new PlacesNearbyHttpAction(location))
            .map(PlacesNearbyHttpAction::response)
            .map(NearbyResponse::locationPlaces)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
