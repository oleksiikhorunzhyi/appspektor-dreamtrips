package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import android.net.Uri;

import com.worldventures.dreamtrips.util.ValidationUtils;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;

public class WalletGalleryVideoModel extends WalletGalleryPickerModel {

   protected String absolutePath;
   protected Uri uri;
   protected long dateTaken;
   protected long pickedTime;
   protected boolean checked;
   private long duration;

   public WalletGalleryVideoModel(String absolutePath, long duration, long dateTaken) {
      this.absolutePath = absolutePath;
      this.dateTaken = dateTaken;
      this.uri = Uri.parse(ValidationUtils.isUrl(absolutePath) ? this.absolutePath : "file://" + this.absolutePath);
      this.duration = duration;
   }

   public WalletGalleryVideoModel(String absolutePath, long duration) {
      this(absolutePath, duration, 0);
   }

   @Override
   public Type getType() {
      return Type.VIDEO;
   }

   @Override
   public boolean isChecked() {
      return checked;
   }

   @Override
   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public Uri getUri() {
      return uri;
   }

   @Override
   public String getAbsolutePath() {
      return absolutePath;
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   @Override
   public long getDateTaken() {
      return dateTaken;
   }

   @Override
   public int type(WalletPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   public long getDuration() {
      return duration;
   }
}
