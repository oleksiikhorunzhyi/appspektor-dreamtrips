package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AttributesInteractor {

   private final ActionPipe<AttributesAction> attributesPipe;
   private final FilterDataInteractor filterDataInteractor;
   private final DtlLocationInteractor dtlLocationInteractor;

   public AttributesInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         FilterDataInteractor filterDataInteractor, DtlLocationInteractor dtlLocationInteractor) {

      this.filterDataInteractor = filterDataInteractor;
      this.dtlLocationInteractor = dtlLocationInteractor;

      attributesPipe = sessionActionPipeCreator.createPipe(AttributesAction.class, Schedulers.io());

      connectLocationChange();
   }

   public void requestAmenities() {
      Observable.combineLatest(
            dtlLocationInteractor.locationSourcePipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .map(dtlLocationCommand -> dtlLocationCommand.getResult().getCoordinates()),
            filterDataInteractor.filterDataPipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .map(FilterDataAction::getResult),
            (location, filterData) ->
                  new AttributesAction(location.getLat() + "," + location.getLng(),
                        FilterHelper.provideDistanceByIndex(filterData),
                        AttributeType.AMENITY.toString().toLowerCase()))
            .take(1)
            .subscribe(attributesAction -> attributesPipe.send(attributesAction));
   }

   public ReadActionPipe<AttributesAction> attributesPipe() {
      return attributesPipe;
   }

   private void connectLocationChange() {
      dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .filter(DtlLocationCommand::isResultDefined)
            .map(DtlLocationCommand::getResult)
            .subscribe(dtlLocation -> requestAmenities());
   }
}
