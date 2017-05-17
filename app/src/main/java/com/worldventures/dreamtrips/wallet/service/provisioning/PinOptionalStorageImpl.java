package com.worldventures.dreamtrips.wallet.service.provisioning;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;

public class PinOptionalStorageImpl implements PinOptionalStorage {

   private final SnappyRepository snappyRepository;

   public PinOptionalStorageImpl(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public boolean shouldAskForPin() {
      return snappyRepository.shouldAskForPin();
   }

   @Override
   public void saveShouldAskForPin(boolean shouldAskForPin) {
      snappyRepository.saveShouldAskForPin(shouldAskForPin);
   }
}
