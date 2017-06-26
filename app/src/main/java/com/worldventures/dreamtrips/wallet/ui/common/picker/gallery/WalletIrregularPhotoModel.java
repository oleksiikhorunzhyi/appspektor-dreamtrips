package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class WalletIrregularPhotoModel extends WalletGalleryPickerModel {

   public static final int CAMERA = 1;
   public static final int FACEBOOK = 2;

   private int iconRes;
   private int titleRes;
   private int colorRes;

   private int attachType;

   public WalletIrregularPhotoModel(@AttachType int attachType, @DrawableRes int iconRes, @StringRes int titleRes, @ColorRes int colorRes) {
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

   @WalletIrregularPhotoModel.AttachType
   public int getType() {
      return attachType;
   }

   @IntDef({CAMERA, FACEBOOK})
   @Retention(RetentionPolicy.SOURCE)
   protected @interface AttachType {}

   @Override
   public int type(WalletPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public boolean isChecked() {
      throw new UnsupportedOperationException("WalletIrregularPhotoModel doesn't support check");
   }

   @Override
   public void setChecked(boolean checked) {
      throw new UnsupportedOperationException("WalletIrregularPhotoModel doesn't support check");
   }

   @Override
   public String getImageUri() {
      throw new UnsupportedOperationException("WalletIrregularPhotoModel doesn't support imageUri");
   }

   @Override
   public String getAbsolutePath() {
      throw new UnsupportedOperationException("WalletIrregularPhotoModel doesn't support path");
   }

   @Override
   public long getPickedTime() {
      throw new UnsupportedOperationException("WalletIrregularPhotoModel doesn't support pick time");
   }

   @Override
   public void setPickedTime(long pickedTime) {
      throw new UnsupportedOperationException("WalletIrregularPhotoModel doesn't support pick time");
   }
}
