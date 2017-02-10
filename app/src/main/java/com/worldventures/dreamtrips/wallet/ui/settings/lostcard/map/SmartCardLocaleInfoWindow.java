package com.worldventures.dreamtrips.wallet.ui.settings.lostcard.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.util.List;

import static java.lang.String.format;

public class SmartCardLocaleInfoWindow implements GoogleMap.InfoWindowAdapter {

   private Context context;
   private LostCardPin lostCardPin;

   public SmartCardLocaleInfoWindow(Context context, LostCardPin lostCardPin) {
      this.context = context;
      this.lostCardPin = lostCardPin;
   }

   @Override
   public View getInfoWindow(Marker marker) {
      View viewMarker = LayoutInflater.from(context).inflate(R.layout.map_popup_info_view, null);

      viewMarker.findViewById(R.id.tv_empty_last_location_msg).setVisibility(View.GONE);
      viewMarker.findViewById(R.id.destination_label_container).setVisibility(View.VISIBLE);

      TextView tvPlace = (TextView) viewMarker.findViewById(R.id.tv_place);

      List<WalletPlace> places = lostCardPin.places();
      if (places.size() == 1) {
         tvPlace.setVisibility(View.VISIBLE);
         tvPlace.setText(places.get(0).name());
      } else {
         tvPlace.setVisibility(View.GONE);
      }

      if (lostCardPin.address() != null) {
         final WalletAddress address = lostCardPin.address();
         ((TextView) viewMarker.findViewById(R.id.tv_info)).setText(
               format("%s, %s\n%s", address.countryName(), address.adminArea(), address.addressLine())
         );
      }

      return viewMarker;
   }

   public void openExternalMap() {
      Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
            + lostCardPin.position().lat() + "," + lostCardPin.position().lng() + "?z=17&q="
            + lostCardPin.position().lat() + "," + lostCardPin.position().lng()));
      context.startActivity(map);
   }

   @Override
   public View getInfoContents(Marker marker) {
      return null;
   }
}
