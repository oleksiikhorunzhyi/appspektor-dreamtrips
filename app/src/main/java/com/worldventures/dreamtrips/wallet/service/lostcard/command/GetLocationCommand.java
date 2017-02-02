package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.smart_card.location.GetSmartCardLocationsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.lostcard.SCLocationRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;


public class GetLocationCommand extends Command<List<SmartCardLocation>> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject SCLocationRepository locationRepository;
   @Inject SystemPropertiesProvider propertiesProvider;

   @Override
   protected void run(CommandCallback<List<SmartCardLocation>> callback) throws Throwable {
      Observable.concat(getStoredLocation(), getHistoricalLocation())
            .map(this::sortListByDate)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<List<SmartCardLocation>> getHistoricalLocation() {
      return janet.createPipe(GetSmartCardLocationsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetSmartCardLocationsHttpAction(Long.parseLong(propertiesProvider.deviceId())))
            .map(GetSmartCardLocationsHttpAction::response);
   }

   private Observable<List<SmartCardLocation>> getStoredLocation() {
      return Observable.just(Collections.singletonList(locationRepository.getSmartCardLocation()));
   }

   private List<SmartCardLocation> sortListByDate(List<SmartCardLocation> smartCardLocations) {
      return Queryable.from(smartCardLocations)
            .distinct()
            .sort((smartCardLocation1, smartCardLocation2) -> smartCardLocation1.createdAt().compareTo(smartCardLocation2.createdAt()))
            .toList();
   }
}
