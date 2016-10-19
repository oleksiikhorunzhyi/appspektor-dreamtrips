package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.List;

abstract class BaseItemProvider {

   public abstract List<SettingsRadioModel> items();

   public abstract int getDefaultPosition();

   public abstract @StringRes int getDefaultTextId();

   public int getPositionForValue(long value) {
      List<SettingsRadioModel> items = items();
      for (int i = 0; i < items.size(); i++) {
         if (value == items.get(i).getValue()) return i;
      }
      return getDefaultPosition();
   }

   @StringRes
   public int provideTextByValue(long value) {
      List<SettingsRadioModel> items = items();
      for (int i = 0; i < items.size(); i++) {
         if (value == items.get(i).getValue()) return items.get(i).getStringId();
      }
      return getDefaultTextId();
   }
}
