package com.worldventures.dreamtrips.social.ui.feed.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PickerIrregularPhotoModel {

   public static final int CAMERA = 1;
   public static final int FACEBOOK = 2;
   public static final int LIBRARY = 3;

   private final int iconRes;
   private final int titleRes;
   private final int colorRes;
   private final int attachType;

   public PickerIrregularPhotoModel(@AttachType int attachType, @DrawableRes int iconRes, @StringRes int titleRes, @ColorRes int colorRes) {
      this.attachType = attachType;
      this.iconRes = iconRes;
      this.titleRes = titleRes;
      this.colorRes = colorRes;
   }

   public int getIconRes() {
      return iconRes;
   }

   public int getTitleRes() {
      return titleRes;
   }

   public int getColorRes() {
      return colorRes;
   }

   @AttachType
   public int getType() {
      return attachType;
   }

   @IntDef({CAMERA, FACEBOOK, LIBRARY})
   @Retention(RetentionPolicy.SOURCE)
   protected @interface AttachType {}
}
