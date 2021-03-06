package com.worldventures.dreamtrips.social.ui.bucketlist.view.util;

import android.app.Activity;
import android.app.Dialog;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetDialogHelper {

   public void notifyItemAddedToBucket(Activity activity, BucketItem item) {
      Dialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE).setTitleText(activity
            .getString(R.string.congrats))
            .setContentText(activity.getString(R.string.bucket_added, item.getName()))
            .setCustomImage(R.drawable.ic_trip_add_to_bucket_selected);
      sweetAlertDialog.setCanceledOnTouchOutside(true);
      sweetAlertDialog.show();
   }

   public void notifyTripLiked(Activity activity, String name, boolean isLiked) {
      Dialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE).setTitleText(activity
            .getString(R.string.congrats))
            .setContentText(activity.getString(isLiked ? R.string.trip_liked : R.string.trip_unliked, name))
            .setCustomImage(isLiked ? R.drawable.ic_trip_like_selected : R.drawable.ic_trip_like_shadow_normal);
      sweetAlertDialog.setCanceledOnTouchOutside(true);
      sweetAlertDialog.show();
   }

}
