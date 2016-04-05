package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlMapPresenter extends FlowPresenter<DtlMapScreen, ViewState.EMPTY> {

    void onMapLoaded();

    void applySearch(String query);

    void onSearchClick();

    void onMarkerClick(String merchantId);

    void onLoadMerchantsClick(LatLng cameraPosition);
}
