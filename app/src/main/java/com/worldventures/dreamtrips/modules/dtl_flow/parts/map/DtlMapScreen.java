package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import rx.Observable;

public interface DtlMapScreen extends DtlScreen {

    GoogleMap getMap();

    void showProgress(boolean show);

    void addLocationMarker(LatLng location);

    void addPin(String id, LatLng latLng, DtlMerchantType type);

    void clearMap();

    void prepareInfoWindow(int height);

    void centerIn(Location location);

    void renderPins();

    void hideDinings(boolean hide);

    void cameraPositionChange(CameraPosition cameraPosition);

    void markerClick(Marker marker);

    void showButtonLoadMerchants(boolean show);

    void zoom(float zoom);

    void updateToolbarTitle(@Nullable DtlLocation dtlLocation);

    void tryHideMyLocationButton(boolean hide);

    void animateTo(LatLng coordinates, int offset);

    Observable<Boolean> getToggleObservable();

    void showPinInfo(String merchantId);
}
