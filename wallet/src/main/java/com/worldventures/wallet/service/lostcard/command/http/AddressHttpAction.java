package com.worldventures.wallet.service.lostcard.command.http;

import com.worldventures.core.janet.BaseThirdPartyHttpAction;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.service.lostcard.command.http.model.AddressRestResponse;

import java.util.Locale;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

@HttpAction(method = HttpAction.Method.GET)
public class AddressHttpAction extends BaseThirdPartyHttpAction {

   @Url String mapApiAddress = "https://maps.googleapis.com/maps/api/geocode/json";

   @Query("latlng") String coordinates;
   @Query("key") String apiKey;

   @Response AddressRestResponse response;

   public AddressHttpAction(String apiKey, WalletCoordinates location) {
      this.apiKey = apiKey;
      this.coordinates = String.format(Locale.US, "%.6f,%,6f", location.getLat(), location.getLng());
   }

   public AddressRestResponse response() {
      return response;
   }
}
