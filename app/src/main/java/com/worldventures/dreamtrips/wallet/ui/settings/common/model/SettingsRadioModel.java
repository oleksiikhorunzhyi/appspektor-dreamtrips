package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

public class SettingsRadioModel {

   private final int textResId;
   private final long value;

   public SettingsRadioModel(int textResId, long value) {
      this.textResId = textResId;
      this.value = value;
   }

   public int getTextResId() {
      return textResId;
   }

   public long getValue() {
      return value;
   }
}
