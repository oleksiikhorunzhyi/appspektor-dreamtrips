package com.worldventures.core.modules.picker.viewmodel;

import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import com.worldventures.core.modules.picker.util.adapter.MediaPickerHolderFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class IrregularPhotoPickerViewModel extends GalleryMediaPickerViewModel {

   public static final int CAMERA = 1;
   public static final int FACEBOOK = 2;

   private final int iconRes;
   private final int titleRes;
   private final int colorRes;
   private final int attachType;

   public IrregularPhotoPickerViewModel(@AttachType int attachType, @DrawableRes int iconRes, @StringRes int titleRes, @ColorRes int colorRes) {
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

   @IrregularPhotoPickerViewModel.AttachType
   public int getAttachType() {
      return attachType;
   }

   @IntDef({CAMERA, FACEBOOK})
   @Retention(RetentionPolicy.SOURCE)
   protected @interface AttachType {}

   @Override
   public int type(MediaPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public Type getType() {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support type");
   }

   @Override
   public boolean isChecked() {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support check");
   }

   @Override
   public void setChecked(boolean checked) {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support check");
   }

   @Override
   public Uri getUri() {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support imageUri");
   }

   @Override
   public String getAbsolutePath() {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support path");
   }

   @Override
   public long getPickedTime() {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support pick time");
   }

   @Override
   public long getDateTaken() {
      return 0;
   }

   @Override
   public void setPickedTime(long pickedTime) {
      throw new UnsupportedOperationException("IrregularPhotoModel doesn't support pick time");
   }
}
