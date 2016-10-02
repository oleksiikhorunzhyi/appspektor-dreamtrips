package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingExpandEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingViewEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.MerchantByIdParamsHolder;
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
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.ThinMerchantsCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
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
   @State MerchantByIdParamsHolder actionParamsHolder;

   public DtlMerchantsPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onNewViewState() {
      state = new DtlMerchantsState();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setExpandedMerchantIds(getView().getExpandedOffers());
      super.onSaveInstanceState(bundle);
   }

   @Override
   public void onRestoreInstanceState(Bundle instanceState) {
      super.onRestoreInstanceState(instanceState);
      getView().setExpandedOffers(state.getExpandedMerchantIds());
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      apiErrorPresenter.setView(getView());

      merchantInteractor.thinMerchantsHttpPipe()
            .observe()
            .compose(bindView())
            .compose(new ActionStateToActionTransformer())
            .subscribe(merchantsAction -> analyticsInteractor.dtlAnalyticsCommandPipe()
                  .send(DtlAnalyticsCommand.create(new MerchantsListingViewEvent())));

      connectService();
      connectFilterDataChanges();

      merchantInteractor.thinMerchantsHttpPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionStateToActionTransformer<>())
            .map(ThinMerchantsCommand::getResult)
            .filter(List::isEmpty)
            .subscribe(s -> showEmptyView(), e -> {
            });
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .subscribe(getView()::updateToolbarSearchCaption);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::isOffersOnly)
            .subscribe(getView()::toggleOffersOnly);
      fullMerchantInteractor.fullMerchantPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MerchantByIdCommand>()
                  .onSuccess(this::onSuccessMerchantLoad)
                  .onProgress(this::onProgressMerchantLoad)
                  .onFail(this::onFailMerchantLoad));
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(ThinMerchantsCommand::getResult)
            .subscribe(items -> getView().setItems(items));

      bindToolbarLocationCaptionUpdates();
   }

   private void bindToolbarLocationCaptionUpdates() {
      locationInteractor.locationPipe()
            .observeSuccess()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
   }

   private void connectFilterDataChanges() {
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .map(FilterDataAction::getResult)
            .map(FilterData::isDefault)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::setFilterButtonState);
   }

   private void connectService() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ThinMerchantsCommand>()
                  .onStart(action -> getView().showProgress())
                  .onFail(apiErrorPresenter::handleActionError));
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
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new MerchantsListingExpandEvent(merchant.asMerchantAttributes())));
   }

   @Override
   public void retryLoadMerchant() {
      if (actionParamsHolder == null) return;

      fullMerchantInteractor.load(MerchantByIdParamsHolder.toAction(actionParamsHolder));
   }

   @Override
   public void merchantClicked(ThinMerchant merchant) {
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .filter(query -> !TextUtils.isEmpty(query))
            .subscribe(query -> analyticsInteractor.dtlAnalyticsCommandPipe()
                  .send(DtlAnalyticsCommand.create(new MerchantFromSearchEvent(query))));

      loadMerchant(merchant, null);
   }

   private void loadMerchant(ThinMerchant merchant, @Nullable String expandedOfferId) {
      fullMerchantInteractor.load(MerchantByIdCommand.create(merchant.id(), expandedOfferId));
   }

   private void showEmptyView() {
      if (!getView().isTabletLandscape() && getView().getPath().isAllowRedirect()) {
         navigateToPath(new DtlLocationChangePath());
      } else {
         getView().showEmptyMerchantView(true);
      }
   }

   @SuppressWarnings("unused")
   protected void onProgressMerchantLoad(CommandWithError<Merchant> action, Integer progress) {
      getView().showBlockingProgress();
   }

   @SuppressWarnings("unused")
   protected void onFailMerchantLoad(MerchantByIdCommand command, Throwable throwable) {
      actionParamsHolder = MerchantByIdParamsHolder.fromAction(command);
      //
      getView().hideBlockingProgress();
      getView().showError(command.getErrorMessage());
   }

   @SuppressWarnings("unused")
   protected void onSuccessMerchantLoad(MerchantByIdCommand command) {
      getView().hideBlockingProgress();
      navigateToDetails(command.getResult(), command.getOfferId());
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

   protected void navigateToPath(Path path) {
      History history = History.single(path);
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   public void onEventMainThread(ToggleMerchantSelectionEvent event) {
      //getView().toggleSelection(event.getDtlMerchant()); TODO :: toggle selection
   }
}
