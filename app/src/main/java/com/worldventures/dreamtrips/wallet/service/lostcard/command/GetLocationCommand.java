package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.api.smart_card.location.GetSmartCardLocationsHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class GetLocationCommand extends Command<List<WalletLocation>> implements InjectableAction {

   @Inject Janet janet;
   @Inject LostCardRepository locationRepository;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject MapperyContext mapperyContext;

   @Override
   protected void run(CommandCallback<List<WalletLocation>> callback) throws Throwable {
      Observable.concat(getStoredLocation(), getHistoricalLocation())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<List<WalletLocation>> getHistoricalLocation() {
      return observeActiveSmartCard()
            .flatMap(command -> observeGetSmartCardLocations(command.getResult().smartCardId()))
            .map(GetSmartCardLocationsHttpAction::response)
            .map(locations -> mapperyContext.convert(locations, WalletLocation.class))
            .onErrorReturn(throwable -> Collections.emptyList());
   }

   private Observable<ActiveSmartCardCommand> observeActiveSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand());
   }

   private Observable<GetSmartCardLocationsHttpAction> observeGetSmartCardLocations(String smartCardId) {
      return janet.createPipe(GetSmartCardLocationsHttpAction.class)
            .createObservableResult(new GetSmartCardLocationsHttpAction(Long.parseLong(smartCardId)));
   }

   private Observable<List<WalletLocation>> getStoredLocation() {
      return Observable.just(locationRepository.getWalletLocations())
            .flatMap(locations -> {
               if (locations.isEmpty()) {
                  return Observable.empty();
               } else {
                  return Observable.just(locations);
               }
            });
   }
}
