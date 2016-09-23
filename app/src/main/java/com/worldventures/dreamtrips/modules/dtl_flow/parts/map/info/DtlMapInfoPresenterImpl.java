package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.event.ShowMapInfoAction;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import javax.inject.Inject;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlFilterMerchantInteractor filterInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject PresentationInteractor presentationInteractor;
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
      //
      presentationInteractor.showMapInfoPipe()
            .observeSuccess()
            .compose(bindView())
            .subscribe(action -> getView().visibleLayout(true));
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
      presentationInteractor.mapPopupReadyPipe().send(MapInfoReadyAction.create(height));
   }
}
