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
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

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

      if (lostCardPin.place() != null) {
         tvPlace.setVisibility(View.VISIBLE);
         tvPlace.setText(lostCardPin.place());
      } else {
         tvPlace.setVisibility(View.GONE);
      }

      if (lostCardPin.address() != null) {
         ((TextView) viewMarker.findViewById(R.id.tv_info)).setText(lostCardPin.address());
      }

      viewMarker.findViewById(R.id.btn_directions)
            .setOnClickListener(view -> onOpenExternalMap());

      return viewMarker;
   }

   private void onOpenExternalMap() {
      Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
            + lostCardPin.position().latitude + "," + lostCardPin.position().longitude + "?z=17&q="
            + lostCardPin.position().latitude + "," + lostCardPin.position().longitude));
      context.startActivity(map);
   }

   @Override
   public View getInfoContents(Marker marker) {
      return null;
   }
}
