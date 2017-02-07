package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.List;

public class DiskLocationRepository implements SCLocationRepository {

   private final SnappyRepository snappyRepository;

   public DiskLocationRepository(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public List<SmartCardLocation> getSmartCardLocations() {
      return snappyRepository.getSmartCardLocations();
   }

   @Override
   public void saveSmartCardLocations(List<SmartCardLocation> smartCardLocations) {
      snappyRepository.saveSmartCardLocations(smartCardLocations);
   }

   @Override
   public void saveEnabledTracking(boolean enable) {
      snappyRepository.saveEnabledTracking(enable);
   }

   @Override
   public boolean isEnableTracking() {
      return snappyRepository.isEnableTracking();
   }
}
