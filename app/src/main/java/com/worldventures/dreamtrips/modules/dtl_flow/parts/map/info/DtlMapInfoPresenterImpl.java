package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantFromSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.event.MapInfoReadyAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import javax.inject.Inject;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

   @Inject MerchantsInteractor merchantInteractor;
   @Inject FullMerchantInteractor fullMerchantInteractor;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject PresentationInteractor presentationInteractor;

   private final ThinMerchant merchant;

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
      trackIfNeeded();
      fullMerchantInteractor.load(merchant.id());
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

   private @Nullable LatLng mapCoordinates() {
      final Coordinates coordinates = merchant.coordinates();
      if (coordinates == null) return null;
      return new LatLng(coordinates.lat(), coordinates.lng());
   }

   @Override
   public void onSizeReady(int height) {
      presentationInteractor.mapPopupReadyPipe().send(MapInfoReadyAction.create(mapCoordinates(), height));
   }

}
