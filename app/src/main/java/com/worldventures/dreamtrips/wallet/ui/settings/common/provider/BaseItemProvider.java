package com.worldventures.dreamtrips.wallet.ui.settings.common.provider;

import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

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
         if (value == items.get(i).getValue()) return i;
      }
      return 0;
   }

   public final String provideTextByValue(long value) {
      final List<SettingsRadioModel> items = items();
      for (int i = 0; i < items.size(); i++) {
         if (value == items.get(i).getValue()) return items.get(i).getText();
      }
      return items.get(0).getText();
   }
}
