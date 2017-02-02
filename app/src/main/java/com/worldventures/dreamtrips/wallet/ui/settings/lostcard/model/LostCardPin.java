package com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model;

import com.google.android.gms.maps.model.LatLng;

import org.immutables.value.Value;

@Value.Immutable
public interface LostCardPin {

   String place();

   String address();

   LatLng position();
}
