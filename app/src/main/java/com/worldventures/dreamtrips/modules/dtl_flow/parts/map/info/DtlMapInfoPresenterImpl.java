package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import javax.inject.Inject;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject PresentationInteractor presentationInteractor;

   protected ThinMerchant merchant;

   public DtlMapInfoPresenterImpl(Context context, Injector injector, ThinMerchant merchant) {
      super(context);
      injector.inject(this);
      this.merchant = merchant;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setMerchant(merchant);

      presentationInteractor.showMapInfoPipe()
            .observeSuccess()
            .compose(bindView())
            .subscribe(action -> getView().visibleLayout(true));
   }

   @Override
   public void onMarkerClick() {
      eventBus.post(new ToggleMerchantSelectionEvent(merchant));
      trackIfNeeded();
      fullMerchantInteractor.load(FullMerchantAction.create(merchant.id()));
   }

   private void trackIfNeeded() {
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindView())
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .filter(query -> !TextUtils.isEmpty(query))
            .subscribe(query -> analyticsInteractor.dtlAnalyticsCommandPipe()
                  .send(DtlAnalyticsCommand.create(new MerchantFromSearchEvent(query))));
   }

   @Override
   public void onSizeReady(int height) {
      presentationInteractor.mapPopupReadyPipe().send(MapInfoReadyAction.create(height));
   }
}
