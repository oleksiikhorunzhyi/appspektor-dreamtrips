package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.PersistentCardListStorage;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;

import java.util.List;

public class WalletCardsDiskStorage implements ActionStorage<List<Card>> {

   private final PersistentCardListStorage cardListStorage;

   public WalletCardsDiskStorage(PersistentCardListStorage cardListStorage) {
      this.cardListStorage = cardListStorage;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, List<Card> data) {
      cardListStorage.saveWalletCardsList(data);
   }

   @Override
   public synchronized List<Card> get(@Nullable CacheBundle bundle) {
      return cardListStorage.readWalletCardsList();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return CardListCommand.class;
   }

}