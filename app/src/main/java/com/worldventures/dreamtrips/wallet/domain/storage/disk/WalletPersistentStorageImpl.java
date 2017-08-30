package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.entity.AboutSmartCardData;

public class WalletPersistentStorageImpl extends CryptedModelStorage implements WalletPersistentStorage {

   private final String WALLET_ABOUT_SMART_CARD_DATA = "WALLET_ABOUT_SMART_CARD_DATA";

   public WalletPersistentStorageImpl(SnappyStorage storage, SnappyCrypter snappyCrypter) {
      super(storage, snappyCrypter);
   }

   @Override
   public void saveAboutSmartCardData(AboutSmartCardData aboutSmartCardData) {
      putEncrypted(WALLET_ABOUT_SMART_CARD_DATA, aboutSmartCardData);
   }

   @Override
   public AboutSmartCardData getAboutSmartCardData() {
      return getEncrypted(WALLET_ABOUT_SMART_CARD_DATA, AboutSmartCardData.class);
   }

   @Override
   public boolean migrate(DB db, int oldVersion) throws SnappydbException {
      return true;
   }

   @Override
   public String getKey() {
      return WALLET_ABOUT_SMART_CARD_DATA;
   }

   @Override
   public int getVersion() {
      return 0;
   }
}
