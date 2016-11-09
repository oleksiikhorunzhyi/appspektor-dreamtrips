package com.worldventures.dreamtrips.modules.dtl.service;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFilterAppliedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.RequestSourceTypeAction;

import java.util.Collections;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FilterDataInteractor {

   private final AnalyticsInteractor analyticsInteractor;
   private final DtlLocationInteractor dtlLocationInteractor;
   private final SnappyRepository snappyRepository;
   private final MerchantsRequestSourceInteractor merchantsRequestSourceInteractor;
   private final ActionPipe<FilterDataAction> filterDataPipe;

   public FilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         AnalyticsInteractor analyticsInteractor, DtlLocationInteractor dtlLocationInteractor, MerchantsRequestSourceInteractor merchantsRequestSourceInteractor,
         SnappyRepository snappyRepository) {

      this.analyticsInteractor = analyticsInteractor;
      this.dtlLocationInteractor = dtlLocationInteractor;
      this.merchantsRequestSourceInteractor = merchantsRequestSourceInteractor;
      this.snappyRepository = snappyRepository;

      filterDataPipe = sessionActionPipeCreator.createPipe(FilterDataAction.class, Schedulers.io());

      connectLocationChange();
      init();
   }

   public ReadActionPipe<FilterDataAction> filterDataPipe() {
      return filterDataPipe;
   }

   public void reset() {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.builder()
                  .distanceType(FilterHelper.provideDistanceFromSettings(snappyRepository))
                  .isOffersOnly(filterData.isOffersOnly())
                  .build())
            .subscribe(this::send);
   }

   public void mergeAndApply(FilterData newFilterData) {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(newFilterData.page())
                  .withBudgetMin(newFilterData.budgetMin())
                  .withBudgetMax(newFilterData.budgetMax())
                  .withDistanceType(FilterHelper.provideDistanceFromSettings(snappyRepository))
                  .withDistanceMaxIndex(newFilterData.distanceMaxIndex())
                  .withSelectedAmenities(newFilterData.selectedAmenities()))
            .map(filterData -> {
               analyticsInteractor.dtlAnalyticsCommandPipe()
                     .send(DtlAnalyticsCommand.create(new MerchantFilterAppliedEvent(filterData)));
               return filterData;
            })
            .subscribe(this::send);
   }

   public void applySearch(@NonNull final String query) {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData).withPage(0)
                  .withSearchQuery(query))
            .subscribe(this::send);
   }

   public void applyRefreshPaginatedPage() {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(0))
            .subscribe(this::send);
   }

   public void applyNextPaginatedPage() {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(filterData.page() + 1))
            .subscribe(this::send);
   }

   public void applyRetryLoad() {
      getLastFilterObservable().subscribe(this::send);
   }

   public void applyOffersOnly(final boolean isOffersOnly) {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(0)
                  .withIsOffersOnly(isOffersOnly))
            .subscribe(this::send);
   }

   private void connectLocationChange() {
      dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .filter(DtlLocationCommand::isResultDefined)
            .map(DtlLocationCommand::getResult)
            .subscribe(dtlLocation ->
                  getLastFilterObservable()
                        .map(filterData -> ImmutableFilterData.copyOf(filterData)
                              .withPage(0)
                              .withSelectedAmenities(Collections.emptyList()))
                        .subscribe(this::send));
   }

   private void send(FilterData filterData) {
      filterDataPipe.send(new FilterDataAction(filterData));
   }

   private Observable<FilterData> getLastFilterObservable() {
      return filterDataPipe.observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult);
   }

   private void init() {
      send(ImmutableFilterData.builder()
            .distanceType(FilterHelper.provideDistanceFromSettings(snappyRepository))
            .build());
   }
}
