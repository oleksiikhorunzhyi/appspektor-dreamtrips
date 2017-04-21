package com.worldventures.dreamtrips.modules.trips.view.util;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class TripViewInjector {

   @InjectView(R.id.textViewName) TextView textViewName;
   @InjectView(R.id.textViewPlace) TextView textViewPlace;
   @InjectView(R.id.textViewPrice) TextView textViewPrice;
   @Optional @InjectView(R.id.textViewDate) TextView textViewDate;
   @Optional @InjectView(R.id.textViewPoints) TextView textViewPoints;
   @InjectView(R.id.sold_out) View soldOut;
   @Optional @InjectView(R.id.pointsCountLayout) FrameLayout pointsCountLayout;
   @InjectView(R.id.textViewFeatured) TextView textViewFeatured;

   public TripViewInjector(View rootView) {
      ButterKnife.inject(this, rootView);
   }

   public void initTripData(TripModel tripModel) {
      if (textViewPoints != null) {
         if (tripModel.getRewardsLimit() != 0) {
            textViewPoints.setText(String.valueOf(tripModel.getRewardsLimit()));
            pointsCountLayout.setVisibility(View.VISIBLE);
            textViewPoints.setVisibility(View.VISIBLE);
         } else {
            textViewPoints.setVisibility(View.GONE);
            pointsCountLayout.setVisibility(View.GONE);
         }
      }

      textViewFeatured.setVisibility(tripModel.isFeatured() ? View.VISIBLE : View.GONE);
      textViewName.setText(tripModel.getName());

      soldOut.setVisibility(tripModel.isSoldOut() ? View.VISIBLE : View.GONE);

      textViewPlace.setText(tripModel.getLocation().getName());

      if (tripModel.getPrice().getAmount() != 0) {
         textViewPrice.setText(tripModel.getPrice().toString());
      } else {
         textViewPrice.setText("");
      }

      if (textViewDate != null) {
         textViewDate.setText(tripModel.hasMultipleDates() ? String.format(textViewDate.getResources()
                     .getString(R.string.multiple_dates),
               tripModel.getAvailabilityDates().getStartDateString()) : tripModel.getAvailabilityDates().toString());
      }
   }
}
