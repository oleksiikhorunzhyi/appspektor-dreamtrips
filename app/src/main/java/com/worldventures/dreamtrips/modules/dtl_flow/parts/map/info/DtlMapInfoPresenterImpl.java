package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.dtl.merchants.MerchantByIdHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.holder.MerchantByIdParamsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.MerchantMapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
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
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;
import flow.History;
import flow.path.Path;
import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlFilterMerchantInteractor filterInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   //
   protected ThinMerchant merchant;

   public DtlMapInfoPresenterImpl(Context context, Injector injector, ThinMerchant merchant) {
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
      //
      merchantInteractor.merchantByIdHttpPipe().send(MerchantByIdCommand.create(merchant.id()));
   }

   private void trackIfNeeded() {
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .map(DtlFilterData::getSearchQuery)
            .filter(query -> !TextUtils.isEmpty(query))
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
