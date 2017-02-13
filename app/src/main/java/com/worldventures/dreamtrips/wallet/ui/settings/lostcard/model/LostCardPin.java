package com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface LostCardPin {

   List<WalletPlace> places();

   WalletAddress address();

   LatLng position();
}
