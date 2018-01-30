package com.worldventures.wallet.domain.storage.action;

import android.support.v4.util.Pair;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;

public class AddressWithPlacesActionStorage extends MemoryStorage<Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>>
      implements ActionStorage<Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FetchAddressWithPlacesCommand.class;
   }
}
