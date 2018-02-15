package com.worldventures.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.wallet.domain.entity.AboutSmartCardData;

public final class PersistentFirmwareDataStorage extends BaseModelStorage implements FirmwareDataStorage {

   private final static String WALLET_ABOUT_SMART_CARD_DATA = "WALLET_ABOUT_SMART_CARD_DATA";

   public PersistentFirmwareDataStorage(SnappyStorage storage) {
      super(storage);
   }

   @Override
   public void saveAboutSmartCardData(AboutSmartCardData aboutSmartCardData) {
      execute(db -> db.put(WALLET_ABOUT_SMART_CARD_DATA, aboutSmartCardData));
   }

   @Override
   public AboutSmartCardData getAboutSmartCardData() {
      return executeWithResult(db -> db.getObject(WALLET_ABOUT_SMART_CARD_DATA, AboutSmartCardData.class)).orNull();
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
