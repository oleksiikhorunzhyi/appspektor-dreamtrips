package com.worldventures.dreamtrips.modules.settings.util;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.modules.settings.model.SettingsGroup.Type.GENERAL;
import static com.worldventures.dreamtrips.modules.settings.model.SettingsGroup.Type.NOTIFICATIONS;

public class SettingsGroupFactory {

   private Context context;

   public SettingsGroupFactory(Context context) {
      this.context = context;
   }

   public List<SettingsGroup> createSettingsGroups() {
      List<SettingsGroup> settingsGroups = new ArrayList<>();
      settingsGroups.add(new SettingsGroup(GENERAL, getTitleByType(GENERAL)));
      settingsGroups.add(new SettingsGroup(NOTIFICATIONS, getTitleByType(NOTIFICATIONS)));

      return settingsGroups;
   }

   private String getTitleByType(SettingsGroup.Type type) {
      switch (type) {
         case GENERAL:
            return context.getResources().getString(R.string.general);
         case NOTIFICATIONS:
            return context.getResources().getString(R.string.notifications);
         default:
            return "";
      }
   }
}
