package com.worldventures.core.modules.settings.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.modules.settings.model.FlagSetting;
import com.worldventures.core.modules.settings.model.SelectSetting;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SettingsStorageImpl extends BaseSnappyRepository implements SettingsStorage {

   private static final String SETTINGS_KEY = "settings";

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   public SettingsStorageImpl(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Override
   public void saveSettings(List<Setting> settingsList, boolean withClear) {
      act(db -> {
         if (withClear) { internalClear(db); }
         //
         for (Setting settings : settingsList) {
            db.put(SETTINGS_KEY + settings.getType().name() + settings.getName(), settings);
         }
      });
   }

   @Override
   public List<Setting> getSettings() {
      return actWithResult(db -> {
         List<Setting> settingsList = new ArrayList<>();
         String[] keys = db.findKeys(SETTINGS_KEY);
         for (String key : keys) {
            if (key.contains(Setting.Type.FLAG.name())) {
               settingsList.add(db.get(key, FlagSetting.class));
            } else if (key.contains(Setting.Type.SELECT.name())) {
               settingsList.add(db.get(key, SelectSetting.class));
            }
         }
         return settingsList;
      }).or(Collections.emptyList());
   }

   @Override
   public void clearSettings() throws SnappydbException {
      act(this::internalClear);
   }

   private void internalClear(DB db) throws SnappydbException {
      String[] settingsKeys = db.findKeys(SETTINGS_KEY);
      for (String key : settingsKeys) {
         db.del(key);
      }
   }

   @Nullable
   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }
}
