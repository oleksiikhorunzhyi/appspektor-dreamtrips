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
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
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
import io.techery.janet.helper.ActionStateToActionTransformer;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, DtlMerchantsState>
      implements DtlMerchantsPresenter {

   @Inject FilterDataInteractor filterDataInteractor;
   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;

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
   }

   private void connectAnalytics() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccess()
            .compose(bindView())
            .map(s -> new MerchantsListingViewEvent())
            .subscribe(this::sendAnaliticsAction);
   }

   private void connectToolbarUpdates() {
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(DtlLocationCommand::getResult)
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
      locationInteractor.locationPipe()
            .observeSuccess()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
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
                  .onSuccess(this::onSuccessMerchantsLoad)
                  .onProgress(this::onProgressMerchantsLoad)
                  .onFail(this::onFailMerchantsLoad));

//      merchantInteractor.thinMerchantsHttpPipe()
//            .observeWithReplay()
//            .compose(bindViewIoToMainComposer())
//            .subscribe(new ActionStateSubscriber<MerchantsAction>().onSuccess(this::setItems));
   }

   private void setItems(MerchantsAction action) {
      if (action.isRefresh()) getView().setRefreshedItems(action.getResult());
      else getView().addItems(action.getResult());
   }

   protected void onSuccessMerchantsLoad(MerchantsAction action) {
      updateProgress(action.isRefresh(), false);
      if (action.isRefresh() && action.getResult().isEmpty()) showEmptyView();
      setItems(action);
   }

   protected void onProgressMerchantsLoad(MerchantsAction action, Integer progress) {
      updateProgress(action.isRefresh(), true);
      hideErrorView();
      hideEmptyView();
   }

   protected void onFailMerchantsLoad(MerchantsAction action, Throwable throwable) {
      if (action.isRefresh()) getView().clearMerchants();

      updateProgress(action.isRefresh(), false);
      showErrorView(action.isRefresh());
      hideEmptyView();
   }

   protected void updateProgress(boolean isRefresh, boolean isLoading) {
      if (!isRefresh) {
         getView().loadNextProgress(isLoading);
         getView().updateLoadingState(isLoading);
      } else getView().refreshProgress(isLoading);
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
      getView().showError(action.getErrorMessage());
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
   public void retryLoadMerchant() {
      if (actionParamsHolder == null) return;

      fullMerchantInteractor.load(FullMerchantParamsHolder.toAction(actionParamsHolder));
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
      fullMerchantInteractor.load(FullMerchantAction.create(merchant.id(), expandedOfferId));
   }

   private void showEmptyView() {
      if (!getView().isTabletLandscape() && getView().getPath().isAllowRedirect()) {
         navigateToPath(new DtlLocationChangePath());
      } else getView().showEmpty(true);
   }

   private void hideEmptyView() {
      getView().showEmpty(false);
   }

   private void showErrorView(boolean isRefresh) {
      hideErrorView();
      if(isRefresh) getView().refreshMerchantsError(true);
      else getView().loadNextMerchantsError(true);
   }

   private void hideErrorView() {
      getView().refreshMerchantsError(false);
      getView().loadNextMerchantsError(false);
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

   public void onEventMainThread(ToggleMerchantSelectionEvent event) {
      //getView().toggleSelection(event.getDtlMerchant()); TODO :: toggle selection
   }
}
