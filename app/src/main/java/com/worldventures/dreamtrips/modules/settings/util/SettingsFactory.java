package com.worldventures.dreamtrips.modules.settings.util;

import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

import java.util.ArrayList;
import java.util.List;

public class SettingsFactory {

   ////////////////////////////
   // Settings titles
   ////////////////////////////
   public static final String DISTANCE_UNITS = "distance_measurement_unit";
   //
   public static final String FRIEND_REQUEST = "receive_friend_request_notifications";
   public static final String NEW_MESSAGE = "receive_new_message_notifications";
   public static final String PHOTO_TAGGING = "receive_tagged_on_photo_notifications";

   ////////////////////////////
   // Settings select options
   ////////////////////////////
   public static final String MILES = "miles";
   public static final String KILOMETERS = "kilometers";

   public static List<Setting> createSettings(SettingsGroup group) {
      switch (group.getType()) {
         case GENERAL:
            return createGeneralSettings();
         case NOTIFICATIONS:
            return createNotificationSettings();
         default:
            return new ArrayList<>();
      }
   }

   public static List<Setting> createSettings() {
      List<Setting> settings = new ArrayList<>();
      settings.addAll(createGeneralSettings());
      settings.addAll(createNotificationSettings());
      return settings;
   }

   private static List<Setting> createGeneralSettings() {
      List<Setting> settingsList = new ArrayList<>();
      List<String> options = new ArrayList<>();
      options.add(MILES);
      options.add(KILOMETERS);
      settingsList.add(new SelectSetting(DISTANCE_UNITS, Setting.Type.SELECT, MILES, options));

      return settingsList;
   }

   private static List<Setting> createNotificationSettings() {
      List<Setting> settingsList = new ArrayList<>();
      settingsList.add(new FlagSetting(FRIEND_REQUEST, Setting.Type.FLAG, true));
      settingsList.add(new FlagSetting(NEW_MESSAGE, Setting.Type.FLAG, true));
      settingsList.add(new FlagSetting(PHOTO_TAGGING, Setting.Type.FLAG, true));

      return settingsList;
   }
}
