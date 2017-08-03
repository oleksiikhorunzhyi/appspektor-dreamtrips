package com.worldventures.dreamtrips.modules.picker.util;


import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.common.view.util.VideoDurationFormatter;

public class MediaPickerBindingUtils {

   private MediaPickerBindingUtils() {
   }

   @BindingAdapter({"pickerStaticItemIcon"})
   public static void setStaticItemResource(ImageView view, @DrawableRes int iconRes) {
      view.setImageResource(iconRes);
   }


   @BindingAdapter("pickerDisplayUri")
   public static void setImage(SimpleDraweeView draweeView, Uri uri) {
      if (draweeView.getTag() != null) {
         if (uri.equals(draweeView.getTag())) {
            return;
         }
      }

      draweeView.setController(GraphicUtils.provideFrescoResizingController(uri, draweeView.getController(), 100, 100));
      draweeView.setTag(uri);
   }

   @BindingAdapter("pickerVideoDuration")
   public static void setDuration(TextView textView, long duration) {
      textView.setText(VideoDurationFormatter.getFormattedDuration(duration));
   }

}
