package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;

import java.util.List;

public interface LostCardRepository {

   List<WalletLocation> getWalletLocations();

   void saveWalletLocations(List<WalletLocation> walletLocations);

   void saveEnabledTracking(boolean enable);

   boolean isEnableTracking();

   void clear();
}
