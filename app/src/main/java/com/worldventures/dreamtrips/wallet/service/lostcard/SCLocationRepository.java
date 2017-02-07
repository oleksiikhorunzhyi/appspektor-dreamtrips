package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;

import java.util.List;

public interface SCLocationRepository {

   List<SmartCardLocation> getSmartCardLocations();

   void saveSmartCardLocations(List<SmartCardLocation> smartCardLocations);

   void saveEnabledTracking(boolean enable);

   boolean isEnableTracking();
}
