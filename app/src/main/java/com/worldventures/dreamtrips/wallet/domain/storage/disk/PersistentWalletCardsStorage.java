package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.List;

public class PersistentWalletCardsStorage extends CryptedModelStorage {

   private final String WALLET_CARDS_LIST = "WALLET_CARDS_LIST";
   private final String DEFAULT_WALLET_CARD_ID = "DEFAULT_WALLET_CARD_ID";

   public PersistentWalletCardsStorage(SnappyStorage storage, SnappyCrypter snappyCrypter) {
      super(storage, snappyCrypter);
   }

   @Override
   public boolean migrate(DB db, int oldVersion) throws SnappydbException {
      return true;
   }

   @Override
   public String getKey() {
      return WALLET_CARDS_LIST;
   }

   @Override
   public int getVersion() {
      return 0;
   }

   public void saveWalletCardsList(List<Card> items) {
      put(WALLET_CARDS_LIST, items);
   }

   public List<Card> readWalletCardsList() {
      return getList(WALLET_CARDS_LIST);
   }

   public void deleteWalletCardList() {
      execute(db -> db.del(WALLET_CARDS_LIST));
   }

   public void saveWalletDefaultCardId(String id) {
      put(DEFAULT_WALLET_CARD_ID, id);
   }

   public String readWalletDefaultCardId() {
      return get(DEFAULT_WALLET_CARD_ID, String.class);
   }

   public void deleteWalletDefaultCardId() {
      execute(db -> db.del(DEFAULT_WALLET_CARD_ID));
   }

}