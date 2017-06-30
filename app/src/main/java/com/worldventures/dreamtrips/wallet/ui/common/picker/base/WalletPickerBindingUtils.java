package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;

public class WalletPickerBindingUtils {

   private WalletPickerBindingUtils() {
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
}
