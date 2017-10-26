package com.worldventures.wallet.service.lostcard;


import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;

import java.util.List;

public interface LostCardRepository {

   List<WalletLocation> getWalletLocations();

   void saveWalletLocations(List<WalletLocation> walletLocations);

   void saveEnabledTracking(boolean enable);

   boolean isEnableTracking();

   void clear();
}
