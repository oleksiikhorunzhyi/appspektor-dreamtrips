package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;

import java.util.List;

public class WalletCardsDiskStorage implements ActionStorage<List<Card>> {

   private final SnappyRepository snappyRepository;

   public WalletCardsDiskStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, List<Card> data) {
      snappyRepository.saveWalletCardsList(data);
   }

   @Override
   public synchronized List<Card> get(@Nullable CacheBundle bundle) {
      return snappyRepository.readWalletCardsList();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return CardListCommand.class;
   }
}
