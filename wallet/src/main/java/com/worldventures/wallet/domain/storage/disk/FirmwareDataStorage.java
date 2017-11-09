package com.worldventures.wallet.domain.storage.disk;

import com.worldventures.wallet.domain.entity.AboutSmartCardData;

public interface FirmwareDataStorage {

   void saveAboutSmartCardData(AboutSmartCardData aboutSmartCardData);

   AboutSmartCardData getAboutSmartCardData();
}
