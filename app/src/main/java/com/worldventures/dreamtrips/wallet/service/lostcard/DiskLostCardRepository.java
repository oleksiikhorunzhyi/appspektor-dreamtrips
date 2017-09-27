package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.di.external.WalletTrackingStatusStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;

import java.util.List;

public class DiskLostCardRepository implements LostCardRepository {

   private final WalletStorage walletStorage;
   private final WalletTrackingStatusStorage trackingStatusStorage;

   public DiskLostCardRepository(WalletStorage walletStorage, WalletTrackingStatusStorage trackingStatusStorage) {
      this.walletStorage = walletStorage;
      this.trackingStatusStorage = trackingStatusStorage;
   }

   @Override
   public List<WalletLocation> getWalletLocations() {
      return walletStorage.getWalletLocations();
   }

   @Override
   public void saveWalletLocations(List<WalletLocation> walletLocations) {
      walletStorage.saveWalletLocations(walletLocations);
   }

   @Override
   public void saveEnabledTracking(boolean enable) {
      trackingStatusStorage.saveEnabledTracking(enable);
   }

   @Override
   public boolean isEnableTracking() {
      return trackingStatusStorage.isEnableTracking();
   }

   @Override
   public void clear() {
      walletStorage.deleteWalletLocations();
   }
}
