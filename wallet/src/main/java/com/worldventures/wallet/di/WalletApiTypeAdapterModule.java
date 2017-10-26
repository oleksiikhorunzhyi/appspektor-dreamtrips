package com.worldventures.wallet.di;

import com.google.gson.TypeAdapterFactory;
import com.worldventures.wallet.service.lostcard.command.http.model.GsonAdaptersAddressRestResponse;
import com.worldventures.wallet.service.lostcard.command.http.model.GsonAdaptersNearbyResponse;

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
