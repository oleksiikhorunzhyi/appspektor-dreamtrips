package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.api.smart_card.location.GetSmartCardLocationsHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class GetLocationCommand extends Command<List<WalletLocation>> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject LostCardRepository locationRepository;
   @Inject SystemPropertiesProvider propertiesProvider;
   @Inject MapperyContext mapperyContext;

   @Override
   protected void run(CommandCallback<List<WalletLocation>> callback) throws Throwable {
      Observable.concat(getStoredLocation(), getHistoricalLocation())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<List<WalletLocation>> getHistoricalLocation() {
      return janet.createPipe(GetSmartCardLocationsHttpAction.class)
            .createObservableResult(new GetSmartCardLocationsHttpAction(Long.parseLong(propertiesProvider.deviceId())))
            .map(GetSmartCardLocationsHttpAction::response)
            .map(locations -> mapperyContext.convert(locations, WalletLocation.class));
   }

   private Observable<List<WalletLocation>> getStoredLocation() {
      return Observable.just(locationRepository.getWalletLocations());
   }
}
