package com.worldventures.wallet.service.lostcard.command.http;


import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.service.lostcard.command.http.model.AddressRestResponse;

import java.util.Locale;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

@HttpAction(method = HttpAction.Method.GET)
public class AddressHttpAction extends BaseThirdPartyHttpAction {

   @Url String mapApiAddress = "http://maps.googleapis.com/maps/api/geocode/json";

   @Query("latlng")
   String coords;
   @Response
   AddressRestResponse response;

   public AddressHttpAction(WalletCoordinates location) {
      this.coords = String.format(Locale.US, "%.6f,%,6f", location.lat(), location.lng());
   }

   public AddressRestResponse response() {
      return response;
   }
}
