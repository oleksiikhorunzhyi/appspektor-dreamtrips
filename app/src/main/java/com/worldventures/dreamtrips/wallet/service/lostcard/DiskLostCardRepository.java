package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;

import java.util.List;

public class DiskLostCardRepository implements LostCardRepository {

   private final SnappyRepository snappyRepository;

   public DiskLostCardRepository(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
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
      snappyRepository.saveEnabledTracking(enable);
   }

   @Override
   public boolean isEnableTracking() {
      return snappyRepository.isEnableTracking();
   }

   @Override
   public void clear() {
      snappyRepository.saveEnabledTracking(false);
      snappyRepository.deleteWalletLocations();
   }
}
