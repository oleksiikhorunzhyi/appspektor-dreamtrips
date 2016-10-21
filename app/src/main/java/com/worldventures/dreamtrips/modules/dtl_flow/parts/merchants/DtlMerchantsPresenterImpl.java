package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsAction;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingExpandEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingViewEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, DtlMerchantsState>
      implements DtlMerchantsPresenter {

   @Inject FilterDataInteractor filterDataInteractor;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;
   @Inject PresentationInteractor presentationInteractor;

   @State boolean initialized;
   @State FullMerchantParamsHolder actionParamsHolder;

   public DtlMerchantsPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state = getView().provideViewState();
      super.onSaveInstanceState(bundle);
   }

   @Override
   public void onRestoreInstanceState(Bundle instanceState) {
      super.onRestoreInstanceState(instanceState);
      getView().applyViewState(state);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      connectMerchants();
      connectAnalytics();
      connectToolbarUpdates();
      connectFullMerchantLoading();
      connectSelections();
   }

   private void connectAnalytics() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccess()
            .compose(bindView())
            .map(s -> new MerchantsListingViewEvent())
            .subscribe(this::sendAnaliticsAction);
   }

   private void connectToolbarUpdates() {
      locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .map(DtlLocationFacadeCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .subscribe(getView()::updateToolbarSearchCaption);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::isOffersOnly)
            .subscribe(getView()::toggleOffersOnly);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .map(FilterDataAction::getResult)
            .map(FilterData::isDefault)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setFilterButtonState);
   }

   private void connectFullMerchantLoading() {
      fullMerchantInteractor.fullMerchantPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FullMerchantAction>()
                  .onSuccess(this::onSuccessMerchantLoad)
                  .onProgress(this::onProgressMerchantLoad)
                  .onFail(this::onFailMerchantLoad));
   }

   private void connectMerchants() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MerchantsAction>()
                  .onStart(this::onStartMerchantsLoad)
                  .onSuccess(this::onMerchantsLoaded)
                  .onProgress(this::onMerchantsLoading)
                  .onFail(this::onMerchantsLoadingError));
   }

   private void onStartMerchantsLoad(MerchantsAction action) {
      if (action.isRefresh()) getView().clearMerchants();
   }

   private void onMerchantsLoaded(MerchantsAction action) {
      if (action.isRefresh()) getView().onRefreshSuccess();
      else getView().onLoadNextSuccess();

      setItemsOrRedirect(action.merchants());
   }

   private void onMerchantsLoading(MerchantsAction action, Integer progress) {
      if (action.isRefresh()) getView().onRefreshProgress();
      else getView().onLoadNextProgress();
   }

   private void onMerchantsLoadingError(MerchantsAction action, Throwable throwable) {
      if (!action.isRefresh()) getView().setRefreshedItems(action.merchants());
      if (action.isRefresh()) getView().onRefreshError(action.getErrorMessage());
      else getView().onLoadNextError();
   }

   private void connectSelections() {
      presentationInteractor.toggleSelectionPipe()
            .observeSuccess().subscribe(this::onToggleSelection);
   }

   private void setItemsOrRedirect(List<ThinMerchant> items) {
      if (items.isEmpty()) onEmptyMerchantsLoaded();
      else getView().setRefreshedItems(items);
   }

   protected void onSuccessMerchantLoad(FullMerchantAction action) {
      getView().hideBlockingProgress();
      navigateToDetails(action.getResult(), action.getOfferId());
   }

   protected void onProgressMerchantLoad(CommandWithError<Merchant> action, Integer progress) {
      getView().showBlockingProgress();
   }

   protected void onFailMerchantLoad(FullMerchantAction action, Throwable throwable) {
      actionParamsHolder = FullMerchantParamsHolder.fromAction(action);

      getView().hideBlockingProgress();
      getView().showLoadMerchantError(action.getErrorMessage());
   }

   @Override
   public void offersOnlySwitched(boolean isOffersOnly) {
      filterDataInteractor.applyOffersOnly(isOffersOnly);
   }

   @Override
   public void mapClicked() {
      navigateToPath(new DtlMapPath(FlowUtil.currentMaster(getContext()), getView().isToolbarCollapsed()));
   }

   @Override
   public void refresh() {
      filterDataInteractor.applyRefreshPaginatedPage();
   }

   @Override
   public void loadNext() {
      filterDataInteractor.applyNextPaginatedPage();
   }

   @Override
   public void onRetryMerchantsClick() {
      filterDataInteractor.applyRetryLoad();
   }

   @Override
   public void onRetryDialogDismiss() {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.clear());
   }

   @Override
   public void locationChangeRequested() {
      navigateToPath(new DtlLocationChangePath());
   }

   @Override
   public void applySearch(String query) {
      filterDataInteractor.applySearch(query);
   }

   @Override
   public void onOfferClick(ThinMerchant dtlMerchant, Offer offer) {
      loadMerchant(dtlMerchant, offer.id());
   }

   @Override
   public void onToggleExpand(boolean expand, ThinMerchant merchant) {
      if (!expand) return;
      sendAnaliticsAction(new MerchantsListingExpandEvent(merchant.asMerchantAttributes()));
   }

   @Override
   public void onRetryMerchantClick() {
      if (actionParamsHolder == null) return;

      fullMerchantInteractor.load(actionParamsHolder);
   }

   @Override
   public void merchantClicked(ThinMerchant merchant) {
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .filter(query -> !TextUtils.isEmpty(query))
            .map(MerchantFromSearchEvent::new)
            .subscribe(this::sendAnaliticsAction);

      loadMerchant(merchant, null);
   }

   private void loadMerchant(ThinMerchant merchant, @Nullable String expandedOfferId) {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.select(merchant));
      fullMerchantInteractor.load(merchant.id(), expandedOfferId);
   }

   private void onEmptyMerchantsLoaded() {
      filterDataInteractor.filterDataPipe().observeSuccessWithReplay().take(1)
            .map(FilterDataAction::getResult)
            .map(FilterData::isDefault)
            .subscribe(this::showEmptyOrRedirect);
   }

   private void showEmptyOrRedirect(boolean isFilterDefault) {
      if (!isAllowRedirect() && !isFilterDefault) {
         getView().clearMerchants();
         getView().showEmpty(true);
         getView().showNoMerchantsCaption(isFilterDefault);
      } else navigateToPath(new DtlLocationChangePath());
   }

   private boolean isAllowRedirect() {
      return getView().getPath().isAllowRedirect() && !getView().isTabletLandscape();
   }

   public void navigateToDetails(Merchant merchant, String id) {
      List<String> expandIds = MerchantHelper.buildExpandedOffersIds(id);
      Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, expandIds);
      if (Flow.get(getContext()).getHistory().size() < 2) {
         Flow.get(getContext()).set(path);
      } else {
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }
   }

   protected void sendAnaliticsAction(DtlAnalyticsAction action) {
      analyticsInteractor.dtlAnalyticsCommandPipe().send(DtlAnalyticsCommand.create(action));
   }

   protected void navigateToPath(Path path) {
      History history = History.single(path);
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   protected void onToggleSelection(ToggleMerchantSelectionAction action) {
      if (action.isClearSelection()) getView().clearSelection();
      else getView().toggleSelection(action.getResult());
   }
}
