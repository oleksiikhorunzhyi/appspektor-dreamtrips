package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsAction;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingExpandEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingLoadmoreEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingViewEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.FullMerchantParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.DtlReviewsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, DtlMerchantsState>
      implements DtlMerchantsPresenter {

   @Inject FilterDataInteractor filterDataInteractor;
   @Inject MerchantsInteractor merchantInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;
   @Inject PresentationInteractor presentationInteractor;
   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject AttributesInteractor attributesInteractor;

   @State boolean initialized;
   @State FullMerchantParamsHolder actionParamsHolder;
   @State boolean hasPendingReview;

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
            .flatMap(merchantsAction ->
                  filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
                        .take(1)
                        .map(Command::getResult)
                        .map(FilterData::page)
            )
            .map(page -> page == 0)
            .map(isFirstPage -> isFirstPage ? new MerchantsListingViewEvent() : new MerchantsListingLoadmoreEvent())
            .subscribe(this::sendAnalyticsAction);
   }

   private void connectToolbarUpdates() {
      locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .map(LocationFacadeCommand::getResult)
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
            .doOnCompleted(getView()::connectToggleUpdate)
            .subscribe(getView()::toggleOffersOnly);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .map(FilterDataAction::getResult)
            .map(FilterData::isDefault)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setFilterButtonState);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::getMerchantType)
            .subscribe(getView()::updateMerchantType);
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

   private void onMerchantsLoading(MerchantsAction action, Integer progress){
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
            .observeSuccess()
            .compose(bindView())
            .subscribe(this::onToggleSelection);
   }

   private void setItemsOrRedirect(List<ThinMerchant> items) {
      if (items.isEmpty()) onEmptyMerchantsLoaded();
      else getView().setRefreshedItems(items);
   }

   protected void onSuccessMerchantLoad(FullMerchantAction action) {
      getView().hideBlockingProgress();
      ReviewStorage.updateReviewsPosted(getContext(),
            String.valueOf(appSessionHolder.get().get().getUser().getId()),
            action.getMerchantId(),
            action.getResult().reviews().userHasPendingReview());
      if (!action.getFromRating()) {
         navigateToDetails(action.getResult(), action.getOfferId());
      } else {
         if (!action.getResult().reviews().total().isEmpty() && Integer.parseInt(action.getResult().reviews().total()) > 0) {
            navigateToRatingList(action.getResult());
         } else {
            navigateToCommentRating(action.getResult());
         }
      }
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
   public void onLoadMerchantsType(List<String> merchantType) {
      filterDataInteractor.applyMerchantTypes(merchantType);
   }

   @Override
   public void loadAmenities(List<String> merchantType) {
      attributesInteractor.requestAmenities(merchantType);
   }

   @Override
   public void sendToRatingReview(ThinMerchant merchant) {
      loadMerchant(merchant, null, true);
   }

   @Override
   public void setMerchantType(List<String> merchantType, String searchQuery) {
      filterDataInteractor.searchMerchantType(merchantType, searchQuery);
   }

   @Override
   public void mapClicked() {
      navigateToPath(new DtlMapPath(FlowUtil.currentMaster(getContext())));
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
      loadMerchant(dtlMerchant, offer.id(), false);
   }

   @Override
   public void onToggleExpand(boolean expand, ThinMerchant merchant) {
      if (!expand) return;
      sendAnalyticsAction(new MerchantsListingExpandEvent(merchant.asMerchantAttributes()));
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
            .subscribe(this::sendAnalyticsAction);

      loadMerchant(merchant, null, false);
   }

   private void loadMerchant(ThinMerchant merchant, @Nullable String expandedOfferId, boolean fromRating) {
      presentationInteractor.toggleSelectionPipe().send(ToggleMerchantSelectionAction.select(merchant));
      fullMerchantInteractor.load(merchant.id(), merchant.reviewSummary(), expandedOfferId, fromRating);
   }

   private void onEmptyMerchantsLoaded() {
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult)
            .subscribe(filterData -> showEmptyOrRedirect(filterData.isDefault(), filterData.isOffersOnly()));
   }

   private void showEmptyOrRedirect(boolean isFilterDefault, boolean isOffersOnly) {
      if (!isAllowRedirect(isFilterDefault && !isOffersOnly)) {
         getView().clearMerchants();
         getView().showEmpty(true);
         getView().showNoMerchantsCaption(isFilterDefault, isOffersOnly);
      } else navigateToPath(new DtlLocationChangePath());
   }

   private boolean isAllowRedirect(boolean isFilterDefault) {
      return getView().getPath().isAllowRedirect() && !getView().isTabletLandscape() && isFilterDefault;
   }

   public void navigateToDetails(Merchant merchant, String id) {
      List<String> expandIds = MerchantHelper.buildExpandedOffersIds(id);
      Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, expandIds, "");
      if (Flow.get(getContext()).getHistory().size() < 2) {
         Flow.get(getContext()).set(path);
      } else {
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }
   }

   public void navigateToCommentRating(Merchant merchant) {
      Flow.get(getContext()).set(new DtlCommentReviewPath(merchant));
   }

   public void navigateToRatingList(Merchant merchant) {
      Flow.get(getContext()).set(new DtlReviewsPath(merchant, ""));
   }

   protected void sendAnalyticsAction(DtlAnalyticsAction action) {
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
