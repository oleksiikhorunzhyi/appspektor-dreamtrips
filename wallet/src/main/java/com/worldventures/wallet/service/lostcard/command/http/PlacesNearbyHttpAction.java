package com.worldventures.wallet.service.lostcard.command.http;

import com.worldventures.core.janet.BaseThirdPartyHttpAction;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.service.lostcard.command.http.model.NearbyResponse;

import java.util.Locale;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.GET;

@HttpAction(method = GET)
public class PlacesNearbyHttpAction extends BaseThirdPartyHttpAction {

   @Url String loadPlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

   @Query("location") String location;
   @Query("radius") String radius = "1";
   @Query("key") String apiKey;

   @Response NearbyResponse response;

   public PlacesNearbyHttpAction(String apiKey, WalletCoordinates location) {
      this.apiKey = apiKey;
      this.location = String.format(Locale.US, "%.6f,%,6f", location.getLat(), location.getLng());
   }

   public NearbyResponse response() {
      return response;
   }
}
