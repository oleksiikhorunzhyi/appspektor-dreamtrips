package com.worldventures.dreamtrips.modules.dtl.service;

import android.support.annotation.NonNull;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFilterAppliedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.RequestSourceTypeAction;

import java.util.Collections;
import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FilterDataInteractor implements Initializable {

   private final AnalyticsInteractor analyticsInteractor;
   private final DtlLocationInteractor dtlLocationInteractor;
   private final SettingsStorage settingsStorage;
   private final MerchantsRequestSourceInteractor merchantsRequestSourceInteractor;
   private final ActionPipe<FilterDataAction> filterDataPipe;

   private String searchQuery = "";

   public FilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         AnalyticsInteractor analyticsInteractor, DtlLocationInteractor dtlLocationInteractor, MerchantsRequestSourceInteractor merchantsRequestSourceInteractor,
         SettingsStorage settingsStorage) {

      this.analyticsInteractor = analyticsInteractor;
      this.dtlLocationInteractor = dtlLocationInteractor;
      this.merchantsRequestSourceInteractor = merchantsRequestSourceInteractor;
      this.settingsStorage = settingsStorage;

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
                  .distanceType(FilterHelper.provideDistanceFromSettings(settingsStorage))
                  .isOffersOnly(filterData.isOffersOnly())
                  .merchantType(filterData.getMerchantType())
                  .build())
            .subscribe(this::send);
   }

   public void searchMerchantType(final List<String> merchantType) {
      searchMerchantType(merchantType, searchQuery);
   }

   public void searchMerchantType(final List<String> merchantType, String searchQuery) {
      this.searchQuery = searchQuery;
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.builder()
                  .distanceType(filterData.distanceType())
                  .distanceMaxIndex(filterData.distanceMaxIndex())
                  .isOffersOnly(filterData.isOffersOnly())
                  .budgetMax(filterData.budgetMax())
                  .budgetMin(filterData.budgetMin())
                  .searchQuery(searchQuery)
                  .merchantType(merchantType)
                  .build())
            .subscribe(this::send);
   }

   public void resetAmenities() {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(0)
                  .withSelectedAmenities(Collections.emptyList()))
            .subscribe(this::send);
   }

   public void mergeAndApply(FilterData newFilterData) {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(newFilterData.page())
                  .withBudgetMin(newFilterData.budgetMin())
                  .withBudgetMax(newFilterData.budgetMax())
                  .withDistanceType(FilterHelper.provideDistanceFromSettings(settingsStorage))
                  .withDistanceMaxIndex(newFilterData.distanceMaxIndex())
                  .withSelectedAmenities(newFilterData.selectedAmenities()))
            .map(filterData -> {
               analyticsInteractor.analyticsCommandPipe()
                     .send(DtlAnalyticsCommand.create(new MerchantFilterAppliedEvent(filterData)));
               return filterData;
            })
            .subscribe(this::send);
   }

   public void applySearch(@NonNull final String query) {
      this.searchQuery = query;
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

   public void applyNextPaginatedPageFromMap() {
      changeRequestSourceToMap();
      applyNextPage();
   }

   public void applyNextPaginatedPage() {
      applyNextPage();
   }

   private void applyNextPage() {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(filterData.page() + 1))
            .subscribe(this::send);
   }

   public void applyRetryLoadFromMap() {
      changeRequestSourceToMap();
      getLastFilterObservable().subscribe(this::send);
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

   public void applyMerchantTypes(final List<String> merchantType) {
      getLastFilterObservable()
            .map(filterData -> ImmutableFilterData.copyOf(filterData)
                  .withPage(0)
                  .withMerchantType(merchantType))
            .subscribe(this::send);
   }

   private void changeRequestSourceToMap() {
      merchantsRequestSourceInteractor.requestSourceActionPipe().send(RequestSourceTypeAction.map());
   }

   private void connectLocationChange() {
      dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .filter(LocationCommand::isResultDefined)
            .map(LocationCommand::getResult)
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

   @Override
   public void init() {
      send(ImmutableFilterData.builder()
            .distanceType(FilterHelper.provideDistanceFromSettings(settingsStorage))
            .build());
   }
}
