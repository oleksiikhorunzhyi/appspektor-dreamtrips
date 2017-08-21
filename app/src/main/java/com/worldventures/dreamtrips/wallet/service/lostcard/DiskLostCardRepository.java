package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.di.external.WalletTrackingStatusStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;

import java.util.List;

public class DiskLostCardRepository implements LostCardRepository {

   private final SnappyRepository snappyRepository;
   private final WalletTrackingStatusStorage trackingStatusStorage;

   public DiskLostCardRepository(SnappyRepository snappyRepository, WalletTrackingStatusStorage trackingStatusStorage) {
      this.snappyRepository = snappyRepository;
      this.trackingStatusStorage = trackingStatusStorage;
   }

   @Override
   public List<WalletLocation> getWalletLocations() {
      return snappyRepository.getWalletLocations();
   }

   @Override
   public void saveWalletLocations(List<WalletLocation> walletLocations) {
      snappyRepository.saveWalletLocations(walletLocations);
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
      snappyRepository.deleteWalletLocations();
   }
}
