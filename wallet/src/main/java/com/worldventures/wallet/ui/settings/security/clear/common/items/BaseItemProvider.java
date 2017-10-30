package com.worldventures.wallet.ui.settings.security.clear.common.items;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.List;

abstract class BaseItemProvider {

   private final List<SettingsRadioModel> items;

   protected BaseItemProvider(List<SettingsRadioModel> items) {
      this.items = items;
   }

   public final List<SettingsRadioModel> items() {
      return items;
   }

   public SettingsRadioModel item(int position) {
      return items.get(position);
   }

   public final int getPositionForValue(long value) {
      final List<SettingsRadioModel> items = items();
      for (int i = 0; i < items.size(); i++) {
         if (value == items.get(i).getValue()) {
            return i;
         }
      }
      return 0;
   }

   public final String provideTextByValue(long value) {
      final List<SettingsRadioModel> items = items();
      for (int i = 0; i < items.size(); i++) {
         if (value == items.get(i).getValue()) {
            return items.get(i).getText();
         }
      }
      return items.get(0).getText();
   }

   protected static SettingsRadioModel createRadioModel(Context context, @StringRes int textResId, long delay) {
      return new SettingsRadioModel(context.getString(textResId), delay);
   }
}
