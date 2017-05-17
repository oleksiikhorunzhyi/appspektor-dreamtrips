package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

enum DisplayOptionsEnum {

   PHOTO_ONLY(SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY, R.string.wallet_settings_general_display_photo_only),
   PHOTO_AND_FIRST_NAME(SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME, R.string.wallet_settings_general_display_photo_first_name),
   FULL_NAME_ONLY(SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY, R.string.wallet_settings_general_display_full_name_only),
   FULL_NAME_AND_PHONE(SetHomeDisplayTypeAction.DISPLAY_PHONE_ONLY, R.string.wallet_settings_general_display_full_name_phone);

   private int displayType;
   private int titleRes;

   DisplayOptionsEnum(@SetHomeDisplayTypeAction.HomeDisplayType int displayType, @StringRes int titleRes) {
      this.displayType = displayType;
      this.titleRes = titleRes;
   }

   @SetHomeDisplayTypeAction.HomeDisplayType
   public int getDisplayType() {
      return displayType;
   }

   @StringRes
   public int getTitleRes() {
      return titleRes;
   }

}