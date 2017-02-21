package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.AddressWithPlacesStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardDetailsStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.TermsAndConditionsStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class WalletActionStorageModule {

   @Provides(type = Provides.Type.SET)
   ActionStorage walletCardListStorage(CardListStorage cardListStorage) {
      return new WalletCardsDiskStorage(cardListStorage);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage defaultBankCardStorage(SnappyRepository snappyRepository) {
      return new DefaultBankCardStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage smartCardStorage(SnappyRepository snappyRepository) {
      return new SmartCardActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage smartCardDetailsStorage(SnappyRepository snappyRepository) {
      return new SmartCardDetailsStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage termsAndConditionsStorage(SnappyRepository snappyRepository) {
      return new TermsAndConditionsStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage addressWithPlacesStorage() {
      return new AddressWithPlacesStorage();
   }
}
