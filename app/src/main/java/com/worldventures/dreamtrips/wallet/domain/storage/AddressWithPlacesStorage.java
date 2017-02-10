package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;

public class AddressWithPlacesStorage extends MemoryStorage<Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>>
      implements ActionStorage<Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FetchAddressWithPlacesCommand.class;
   }
}
