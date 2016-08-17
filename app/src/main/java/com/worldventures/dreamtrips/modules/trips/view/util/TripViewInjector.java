package com.worldventures.dreamtrips.modules.trips.view.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class TripViewInjector {

   @InjectView(R.id.textViewName) protected TextView textViewName;
   @InjectView(R.id.textViewPlace) protected TextView textViewPlace;
   @InjectView(R.id.textViewPrice) protected TextView textViewPrice;
   @Optional @InjectView(R.id.textViewDate) protected TextView textViewDate;
   @InjectView(R.id.sold_out) protected ImageView soldOut;
   @InjectView(R.id.textViewPoints) protected TextView textViewPoints;
   @InjectView(R.id.pointsCountLayout) protected FrameLayout pointsCountLayout;
   @InjectView(R.id.textViewFeatured) protected TextView textViewFeatured;

   public TripViewInjector(View rootView) {
      ButterKnife.inject(this, rootView);
   }

   public void initTripData(TripModel tripModel, User currentUser) {
      String reward = tripModel.getRewardsLimit(currentUser);

      if (!TextUtils.isEmpty(reward) && !"0".equals(reward)) {
         textViewPoints.setText(String.valueOf(reward));
         pointsCountLayout.setVisibility(View.VISIBLE);
         textViewPoints.setVisibility(View.VISIBLE);
      } else {
         textViewPoints.setVisibility(View.GONE);
         pointsCountLayout.setVisibility(View.GONE);
      }

      textViewFeatured.setVisibility(tripModel.isFeatured() ? View.VISIBLE : View.GONE);
      textViewName.setText(tripModel.getName());

      soldOut.setVisibility(tripModel.isSoldOut() ? View.VISIBLE : View.GONE);

      textViewPlace.setText(tripModel.getGeoLocation().getName());
      textViewPrice.setText(tripModel.getPrice().toString());
      if (textViewDate != null) {
         textViewDate.setText(tripModel.isHasMultipleDates() ? String.format(textViewDate.getResources()
               .getString(R.string.multiple_dates), tripModel.getAvailabilityDates()
               .getStartDateString()) : tripModel.getAvailabilityDates().toString());
      }
   }
}
