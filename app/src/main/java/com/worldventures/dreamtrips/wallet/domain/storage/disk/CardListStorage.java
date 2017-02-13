package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.List;

public class CardListStorage extends CryptedModelStorage {

   private final String WALLET_CARDS_LIST = "WALLET_CARDS_LIST";

   public CardListStorage(SnappyCrypter snappyCrypter) {
      super(snappyCrypter);
   }

   @Override
   public boolean migrate(DB db, int oldVersion) throws SnappydbException {
      if (oldVersion == 0) {
         db.del(WALLET_CARDS_LIST);
      }
      return true;
   }

   @Override
   public String getKey() {
      return WALLET_CARDS_LIST;
   }

   @Override
   public int getVersion() {
      return 1;
   }

   public void saveWalletCardsList(List<Card> items) {
      putEncrypted(WALLET_CARDS_LIST, items);
   }

   public List<Card> readWalletCardsList() {
      return getEncryptedList(WALLET_CARDS_LIST);
   }

   public void deleteWalletCardList() {
      execute(db -> db.del(WALLET_CARDS_LIST));
   }
}
