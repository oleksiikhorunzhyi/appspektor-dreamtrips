package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;
import com.worldventures.dreamtrips.modules.trips.model.Location;

public interface DtlMapScreen extends DtlScreen {

   void updateToolbarLocationTitle(@Nullable DtlLocation dtlLocation);

   void updateToolbarSearchCaption(@Nullable String searchCaption);

   GoogleMap getMap();

   void prepareMap();

   void showProgress(boolean show);

   void addLocationMarker(LatLng location);

   void addPin(ThinMerchant merchant);

   void clearMap();

   void prepareInfoWindow(int height);

   void centerIn(Location location);

   void renderPins();

   void toggleOffersOnly(boolean enabled);

   void cameraPositionChange(CameraPosition cameraPosition);

   void markerClick(Marker marker);

   void showButtonLoadMerchants(boolean show);

   void zoom(float zoom);

   void tryHideMyLocationButton(boolean hide);

   void animateTo(LatLng coordinates, int offset);

   void showPinInfo(ThinMerchant merchant);

   void showError(String error);

   void openFilter();

   boolean isToolbarCollapsed();

   void setFilterButtonState(boolean enabled);
}
