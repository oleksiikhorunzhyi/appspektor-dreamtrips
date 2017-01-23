package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.AttributesActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableAttributesActionParams;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class AttributesInteractor {

   private final ActionPipe<AttributesAction> attributesPipe;
   private final FilterDataInteractor filterDataInteractor;
   private final DtlLocationInteractor dtlLocationInteractor;

   private static final String RESTAURANT = "restaurant";
   private static final String BAR = "bar";

   public AttributesInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         FilterDataInteractor filterDataInteractor, DtlLocationInteractor dtlLocationInteractor) {

      this.filterDataInteractor = filterDataInteractor;
      this.dtlLocationInteractor = dtlLocationInteractor;

      attributesPipe = sessionActionPipeCreator.createPipe(AttributesAction.class, Schedulers.io());

      connectLocationChange();
   }

   public ReadActionPipe<AttributesAction> attributesPipe() {
      return attributesPipe;
   }

   public void requestAmenities() {
      List<String> merchantType = new ArrayList<>();
      merchantType.add(RESTAURANT);
      merchantType.add(BAR);
      Observable.combineLatest(
            provideFormattedLocationObservable(),
            provideFilterDataObservable(),
            new AttributesUpdateFunc())
            .take(1)
            .map(param -> AttributesAction.create(param, merchantType))
            .subscribe(attributesPipe::send);
   }

   public void requestAmenities(List<String> merchantType) {
      Observable.combineLatest(
            provideFormattedLocationObservable(),
            provideFilterDataObservable(),
            new AttributesUpdateFunc())
            .take(1)
            .map(param -> AttributesAction.create(param, merchantType))
            .subscribe(attributesPipe::send);
   }

   private Observable<String> provideFormattedLocationObservable() {
      return dtlLocationInteractor.locationSourcePipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(LocationCommand::getResult)
            .map(DtlLocation::provideFormattedLocation);
   }

   private Observable<FilterData> provideFilterDataObservable() {
      return filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult);
   }

   private void connectLocationChange() {
      dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .filter(LocationCommand::isResultDefined)
            .map(LocationCommand::getResult)
            .subscribe(dtlLocation -> requestAmenities());
   }

   private final class AttributesUpdateFunc implements Func2<String, FilterData, AttributesActionParams> {

      @Override
      public AttributesActionParams call(String ll, FilterData filterData) {
         return ImmutableAttributesActionParams.builder()
               .radius(FilterHelper.provideMaxDistance(filterData))
               .ll(ll)
               .build();
      }
   }
}
