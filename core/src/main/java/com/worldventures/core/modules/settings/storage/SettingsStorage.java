package com.worldventures.core.modules.settings.storage;


import com.snappydb.SnappydbException;
import com.worldventures.core.modules.settings.model.Setting;

import java.util.List;

public interface SettingsStorage {

   void saveSettings(List<Setting> settingsList, boolean withClear);

   List<Setting> getSettings();

   void clearSettings() throws SnappydbException;
}
