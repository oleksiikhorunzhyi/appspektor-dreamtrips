package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.ArrayList;
import java.util.List;

public class CardListStorage extends CryptedModelStorage {

   private final String WALLET_CARDS_LIST = "WALLET_CARDS_LIST";

   public CardListStorage(SnappyCrypter snappyCrypter) {
      super(snappyCrypter);
   }

   @Override
   public boolean migrate(DB db, int oldVersion) throws SnappydbException {
      List<Card> cardList = snappyCrypter.getEncryptedList(db, WALLET_CARDS_LIST);
      List<Card> migratedCardList = new ArrayList<>(cardList.size());
      for (Card card : cardList) {
         migratedCardList.add(migrateCard(card));
      }
      snappyCrypter.putEncrypted(db,WALLET_CARDS_LIST, migratedCardList);
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

   private Card migrateCard(Card card) {
      if (card instanceof BankCard) {
         return migrateBankCard((BankCard) card);
      }
      throw new RuntimeException("Other card was not supported");
   }

   private BankCard migrateBankCard(BankCard instance) {
      final ImmutableBankCard.Builder builder = ImmutableBankCard.builder()
            .id(instance.id())
            .number(instance.number())
            .category(instance.category())
            .addressInfo(instance.addressInfo())
            .issuerInfo(instance.issuerInfo())
            .cvv(instance.cvv())
            .track1(instance.track1())
            .track2(instance.track2());

      //new fields:
      if (instance.cardNameHolder() != null) builder.cardNameHolder(instance.cardNameHolder());
      if (instance.nickName() != null) builder.nickName(instance.nickName());
      else if (instance.title() != null) builder.nickName(instance.title());

      if (instance.expDate() != null) builder.expDate(instance.expDate());
      else builder.expDate(createExpDate(instance));

      return builder.build();
   }

   private String createExpDate(Card card) {
      if (card.expiryMonth() != 0 && card.expiryYear() != 0) {
         return String.format("%s/%s", card.expiryMonth(), card.expiryYear());
      }
      return ""; // it cannot be happen
   }
}
