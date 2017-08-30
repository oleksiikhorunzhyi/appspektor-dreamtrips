package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.worldventures.dreamtrips.wallet.domain.entity.AboutSmartCardData;

public interface WalletPersistentStorage {

   void saveAboutSmartCardData(AboutSmartCardData aboutSmartCardData);

   AboutSmartCardData getAboutSmartCardData();
}
