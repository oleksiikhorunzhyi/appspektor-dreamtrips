package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

import rx.Observable;

public interface MasterToolbarScreen extends DtlScreen {

    void updateToolbarTitle(@Nullable DtlLocation dtlLocation, @Nullable String appliedSearchQuery);

    void toggleDiningFilterSwitch(boolean enabled);

    void setFilterButtonState(boolean enabled);

    void locationResolutionRequired(Status status);

    void setItems(List<DtlExternalLocation> locations);

    void showProgress();

    void hideProgress();

    void hideNearMeButton();

    void showSearchPopup();

    boolean isSearchPopupShowing();

    Observable<String> provideLocationSearchObservable();
}
