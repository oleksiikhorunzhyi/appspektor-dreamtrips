package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface DtlLocationChangeScreen extends DtlScreen {

   void updateToolbarTitle(@Nullable DtlLocation dtlLocation, @Nullable List<String> selectedMerchantTypes, @Nullable String appliedSearchQuery);

   void locationResolutionRequired(Status status);

   void setItems(List<DtlLocation> locations, boolean showLocationHeader);

   void hideNearMeButton();

   void showProgress();

   void hideProgress();

   void switchVisibilityNoMerchants(boolean visible);

   void switchVisibilityOrCaption(boolean visible);

   Observable<Void> provideMapClickObservable();

   Observable<Boolean> provideMerchantInputFocusLossObservable();

   Observable<Void> provideDtlToolbarCollapsesObservable();

   Observable<String> provideLocationSearchObservable();

   String getMerchantsSearchQuery();
}
