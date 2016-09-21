package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantsListingViewEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.MerchantByIdParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
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
import rx.Observable;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
      implements DtlMerchantsPresenter {

   @Inject DtlFilterMerchantInteractor filterInteractor;
   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   //
   @State boolean initialized;
   @State MerchantByIdParamsHolder actionParamsHolder;

   public DtlMerchantsPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      apiErrorPresenter.setView(getView());
      //
      merchantInteractor.thinMerchantsHttpPipe()
            .observe()
            .compose(bindView())
            .compose(new ActionStateToActionTransformer())
            .subscribe(merchantsAction -> analyticsInteractor.dtlAnalyticsCommandPipe()
                  .send(DtlAnalyticsCommand.create(new MerchantsListingViewEvent())));
      //
      getView().getToggleObservable().subscribe(offersOnly -> filterInteractor.filterDataPipe()
            .send(DtlFilterDataAction.applyOffersOnly(offersOnly)));
      //
      connectService();
      connectFilterDataChanges();
      //
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .filter(command -> !this.initialized)
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(location -> {
               merchantInteractor.thinMerchantsHttpPipe().send(ThinMerchantsCommand.load(location.getCoordinates()
                     .asAndroidLocation()));
               initialized = true;
            }, Throwable::printStackTrace);
      //
      merchantInteractor.thinMerchantsHttpPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionStateToActionTransformer<>())
            .map(ThinMerchantsCommand::getResult)
            .filter(List::isEmpty)
            .subscribe(s -> showEmptyView(), e -> {});
      //
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .compose(bindViewIoToMainComposer())
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::getSearchQuery)
            .subscribe(getView()::updateToolbarSearchCaption);
      //
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::isOffersOnly)
            .subscribe(getView()::toggleDiningFilterSwitch);
      //
      merchantInteractor.merchantByIdHttpPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MerchantByIdCommand>()
                  .onSuccess(this::onSuccessMerchantLoad)
                  .onProgress(this::onProgressMerchantLoad)
                  .onFail(this::onFailMerchantLoad));
      //
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(ThinMerchantsCommand::getResult)
            .subscribe(items -> getView().setItems(items));
      //
      bindToolbarLocationCaptionUpdates(); // TODO :: 15.09.16 not needed?
   }

   private void bindToolbarLocationCaptionUpdates() {
      locationInteractor.locationPipe()
            .observeSuccess()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
   }

   private void connectFilterDataChanges() {
      filterInteractor.filterDataPipe()
            .observeSuccess()
            .map(DtlFilterDataAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(dtlFilterData -> {
               getView().setFilterButtonState(!dtlFilterData.isDefault());
            });
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
   public void mapClicked() {
      navigateToPath(new DtlMapPath(FlowUtil.currentMaster(getContext()), getView().isToolbarCollapsed()));
   }

   @Override
   public void locationChangeRequested() {
      navigateToPath(new DtlLocationChangePath());
   }

   @Override
   public void applySearch(String query) {
      filterInteractor.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
   }

   @Override
   public void onOfferClick(ThinMerchant dtlMerchant, Offer offer) {
      loadMerchant(dtlMerchant, offer.id());
   }

   @Override
   public void retryLoadMerchant() {
      if (actionParamsHolder == null) return;
      //
      merchantInteractor.merchantByIdHttpPipe().send(MerchantByIdParamsHolder.toAction(actionParamsHolder));
   }

   @Override
   public void merchantClicked(ThinMerchant merchant) {
      Observable.combineLatest(filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::getSearchQuery), locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .map(DtlLocationCommand::getResult), Pair::new)
            .compose(bindViewIoToMainComposer())
            .take(1)
            .subscribe(pair -> {
               if (TextUtils.isEmpty(pair.first)) return;
               analyticsInteractor.dtlAnalyticsCommandPipe()
                     .send(DtlAnalyticsCommand.create(new MerchantFromSearchEvent(pair.first)));
            });
      //
      loadMerchant(merchant, null);
   }

   private void loadMerchant(ThinMerchant merchant, @Nullable String expandedOfferId) {
      merchantInteractor.merchantByIdHttpPipe().send(MerchantByIdCommand.create(merchant.id(), expandedOfferId));
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
      merchantInteractor.merchantByIdHttpPipe().clearReplays();
      //
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
