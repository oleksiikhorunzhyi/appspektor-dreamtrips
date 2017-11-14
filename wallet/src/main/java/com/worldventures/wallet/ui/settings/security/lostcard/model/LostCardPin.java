package com.worldventures.wallet.ui.settings.security.lostcard.model;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
public interface LostCardPin {

   @Nullable
   List<WalletPlace> places();

   @Nullable
   WalletAddress address();

   LatLng position();
}
