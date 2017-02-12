package com.worldventures.dreamtrips.wallet.ui.settings.lostcard.map;

import android.annotation.SuppressLint;
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
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.ui.settings.lostcard.model.LostCardPin;

import java.util.List;

import static java.lang.String.format;

public class LostCardInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

   private final Context context;
   private final LostCardPin pinData;

   public LostCardInfoWindowAdapter(Context context, LostCardPin pinData) {
      this.context = context;
      this.pinData = pinData;
   }

   @Override
   public View getInfoWindow(Marker marker) {
      return null;
   }

   @Override
   public View getInfoContents(Marker marker) {
      @SuppressLint("InflateParams")
      final View view = LayoutInflater.from(context).inflate(R.layout.map_popup_info_view, null);

      // TODO: 2/12/17 view does not handle clicks
//      view.findViewById(R.id.btn_directions).setOnClickListener(v -> openExternalMap());

      bindPlaces((TextView) view.findViewById(R.id.tv_place), pinData.places());
      bindAddress((TextView) view.findViewById(R.id.tv_info), pinData.address());

      return view;
   }

   private void bindPlaces(TextView tvPlace, List<WalletPlace> places) {
      if (places.size() == 1) {
         tvPlace.setVisibility(View.VISIBLE);
         tvPlace.setText(places.get(0).name());
      } else {
         tvPlace.setVisibility(View.GONE);
      }
   }

   private void bindAddress(TextView tvAddress, WalletAddress address) {
      if (address != null) {
         tvAddress.setText(format("%s, %s\n%s", address.countryName(), address.adminArea(), address.addressLine()));
      }
   }

   public void openExternalMap() {
      WalletCoordinates position = pinData.position();
      Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
            + position.lat() + "," + position.lng() + "?z=17&q="
            + position.lat() + "," + position.lng()));
      context.startActivity(map);
   }
}
