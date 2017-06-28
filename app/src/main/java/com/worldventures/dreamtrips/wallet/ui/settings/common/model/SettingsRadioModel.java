package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

public class SettingsRadioModel {

   private final String text;
   private final long value;

   public SettingsRadioModel(String text, long value) {
      this.text = text;
      this.value = value;
   }

   public String getText() {
      return text;
   }

   public long getValue() {
      return value;
   }
}
