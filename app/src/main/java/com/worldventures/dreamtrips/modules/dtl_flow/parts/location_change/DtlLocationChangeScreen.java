package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface DtlLocationChangeScreen extends DtlScreen {

    void updateToolbarTitle(@Nullable DtlLocation dtlLocation, @Nullable String appliedSearchQuery);

    void locationResolutionRequired(Status status);

    void setItems(List<DtlExternalLocation> locations);

    void hideNearMeButton();

    void showProgress();

    void hideProgress();

    Observable<Void> provideMapClickObservable();

    Observable<Boolean> provideMerchantInputFocusLossObservable();

    Observable<Void> provideDtlToolbarCollapsesObservable();

    Observable<String> provideLocationSearchObservable();
}
