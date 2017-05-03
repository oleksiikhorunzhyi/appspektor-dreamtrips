package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

import android.support.annotation.StringRes;

public class SettingsRadioModel {

   private final @StringRes int stringId;
   private final long value;

   public SettingsRadioModel(@StringRes int stringId, long value) {
      this.stringId = stringId;
      this.value = value;
   }

   public int getStringId() {
      return stringId;
   }

   public long getValue() {
      return value;
   }
}
