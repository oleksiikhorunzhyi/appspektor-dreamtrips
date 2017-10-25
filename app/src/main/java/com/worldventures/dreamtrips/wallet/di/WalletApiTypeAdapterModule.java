package com.worldventures.dreamtrips.wallet.di;

import com.google.gson.TypeAdapterFactory;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.GsonAdaptersAddressRestResponse;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.GsonAdaptersNearbyResponse;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
class WalletApiTypeAdapterModule {

   @Provides(type = Provides.Type.SET)
   TypeAdapterFactory nearbyResponseTypeAdapterFactory() {
      return new GsonAdaptersNearbyResponse();
   }

   @Provides(type = Provides.Type.SET)
   TypeAdapterFactory addressResponseTypeAdapterFactory() {
      return new GsonAdaptersAddressRestResponse();
   }
}
