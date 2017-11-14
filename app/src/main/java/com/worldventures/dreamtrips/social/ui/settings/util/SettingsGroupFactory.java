package com.worldventures.dreamtrips.social.ui.settings.util;

import android.content.Context;

import com.worldventures.core.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.core.modules.settings.model.SettingsGroup.Type.GENERAL;
import static com.worldventures.core.modules.settings.model.SettingsGroup.Type.NOTIFICATIONS;

public class SettingsGroupFactory {

   private final Context context;

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
