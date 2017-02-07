package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;

public interface SCLocationRepository {

   SmartCardLocation getSmartCardLocation();

   void saveSmartCardLocation(SmartCardLocation smartCardLocation);

   void saveEnabledTracking(boolean enable);

   boolean isEnableTracking();
}
