package com.worldventures.dreamtrips.modules.trips.view.util;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;
import android.widget.CheckedTextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import butterknife.InjectView;

public class TripFeedViewInjector extends TripViewInjector {

   @InjectView(R.id.imageViewTripImage) SimpleDraweeView imageViewTripImage;
   @InjectView(R.id.imageViewLike) CheckedTextView likeView;
   @InjectView(R.id.imageViewAddToBucket) CheckedTextView addToBucketView;

   public TripFeedViewInjector(View rootView) {
      super(rootView);
   }

   @Override
   public void initTripData(TripModel tripModel) {
      super.initTripData(tripModel);
      //
      likeView.setChecked(tripModel.isLiked());
      addToBucketView.setChecked(tripModel.isInBucketList());
      addToBucketView.setEnabled(!tripModel.isInBucketList());

      PointF pointF = new PointF(0.5f, 0.0f);
      imageViewTripImage.getHierarchy().setActualImageFocusPoint(pointF);
      imageViewTripImage.setImageURI(Uri.parse(tripModel.getThumb(imageViewTripImage.getResources())));
   }
}
