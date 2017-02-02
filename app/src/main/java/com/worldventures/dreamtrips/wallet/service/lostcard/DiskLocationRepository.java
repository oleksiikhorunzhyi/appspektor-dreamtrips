package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

public class DiskLocationRepository implements SCLocationRepository {

   private final SnappyRepository snappyRepository;

   public DiskLocationRepository(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public SmartCardLocation getSmartCardLocation() {
      return snappyRepository.getSmartCardLocation();
   }

   @Override
   public void saveSmartCardLocation(SmartCardLocation smartCardLocation) {
      snappyRepository.saveSmartCardLocation(smartCardLocation);
   }
}
