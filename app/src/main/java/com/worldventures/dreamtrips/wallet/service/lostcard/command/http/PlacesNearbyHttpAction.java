package com.worldventures.dreamtrips.wallet.service.lostcard.command.http;

import com.worldventures.dreamtrips.mobilesdk.service.ServiceLabel;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.model.NearbyResponse;

import java.util.Locale;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.GET;

@HttpAction(method = GET)
public class PlacesNearbyHttpAction implements ServiceLabel {

   @Url String loadPlacesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

   @Query("location") String location;
   @Query("radius") String radius = "1";
   @Query("key") String key = "AIzaSyDGK1DDsWvVa661zcnkCUjEJnuw_dzdUjQ";// TODO: 2/8/17 is test key, before release need to exchange

   @Response NearbyResponse response;

   public PlacesNearbyHttpAction(WalletCoordinates location) {
      this.location = String.format(Locale.US, "%.6f,%,6f", location.lat(), location.lng());
   }

   public NearbyResponse response() {
      return response;
   }

   @Override
   public String label() {
      return "non-api-action";
   }
}
