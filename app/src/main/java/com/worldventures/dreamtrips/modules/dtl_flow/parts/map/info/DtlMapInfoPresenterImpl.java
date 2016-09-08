package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.dtl.merchants.MerchantByIdHttpAction;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.MerchantMapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlFilterMerchantInteractor filterInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   //
   protected DtlMerchant merchant;

   public DtlMapInfoPresenterImpl(Context context, Injector injector, DtlMerchant merchant) {
      super(context);
      injector.inject(this);
      this.merchant = merchant;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      //
      getView().setMerchant(merchant);
   }

   public void onEvent(DtlShowMapInfoEvent event) {
      getView().visibleLayout(true);
   }

   @Override
   public void onMarkerClick() {
      eventBus.post(new ToggleMerchantSelectionEvent(merchant));
      trackIfNeeded();
      loadFullMerchant();
   }

   private void loadFullMerchant() {
      merchantInteractor.merchantByIdHttpPipe()
            .createObservableResult(new MerchantByIdHttpAction(merchant.getId()))
            .compose(new IoToMainComposer<>())
            .map(MerchantByIdHttpAction::merchant)
            .map(MerchantMapper.INSTANCE::convert)
            .subscribe(this::navigateToDetails, Throwable::printStackTrace);
   }

   private void navigateToDetails(Merchant merchant) {
      Path path = new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, null);
      if (Flow.get(getContext()).getHistory().size() < 2) {
         Flow.get(getContext()).set(path);
      } else {
         History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
         historyBuilder.pop();
         historyBuilder.push(path);
         Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
      }
   }

   private void trackIfNeeded() {
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::getSearchQuery)
            .filter(TextUtils::isEmpty)
            .flatMap(query -> locationInteractor.locationPipe()
                  .observeSuccessWithReplay()
                  .map(DtlLocationCommand::getResult)
                  .map(location -> new Pair<>(query, location)))
            .subscribe(pair -> {
               analyticsInteractor.dtlAnalyticsCommandPipe()
                     .send(DtlAnalyticsCommand.create(new MerchantFromSearchEvent(pair.first)));
            });
   }

   @Override
   public void onSizeReady(int height) {
      eventBus.post(new DtlMapInfoReadyEvent(height));
   }
}
