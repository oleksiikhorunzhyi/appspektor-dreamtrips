package com.worldventures.dreamtrips.modules.trips.view.util;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import butterknife.InjectView;

public class TripMapViewInjector extends TripViewInjector {

   @InjectView(R.id.tripCover) SimpleDraweeView cover;

   private int coverSize;

   public TripMapViewInjector(View rootView, int coverSize) {
      super(rootView);
      this.coverSize = coverSize;
   }

   @Override
   public void initTripData(TripModel tripModel) {
      super.initTripData(tripModel);
      cover.setImageURI(Uri.parse(tripModel.getThumb(coverSize, coverSize)));
   }
}
