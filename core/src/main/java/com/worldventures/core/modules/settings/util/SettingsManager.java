package com.worldventures.core.modules.settings.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.settings.model.Setting;

import java.util.List;

public final class SettingsManager {

   private SettingsManager() {
   }

   public static List<Setting> merge(List<Setting> fromServer, List<Setting> local) {
      return Queryable.from(fromServer).map(setting -> {
         Setting localSetting = Queryable.from(local).firstOrDefault(setting::equals);
         if (localSetting != null) setting.setType(localSetting.getType());
         return setting;
      }).toList();
   }
}
