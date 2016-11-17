package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.settings.model.SelectSetting;
import com.worldventures.dreamtrips.api.settings.model.Setting;

import org.jetbrains.annotations.NotNull;

import io.techery.mappery.MapperyContext;

public class SettingConverter implements Converter<Setting, com.worldventures.dreamtrips.modules.settings.model.Setting> {
   @Override
   public Class<Setting> sourceClass() {
      return Setting.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.settings.model.Setting> targetClass() {
      return com.worldventures.dreamtrips.modules.settings.model.Setting.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.settings.model.Setting convert(@NotNull MapperyContext mapperyContext, Setting apiSetting) {
      switch (apiSetting.type()) {
         case FLAG:
            return new com.worldventures.dreamtrips.modules.settings.model.FlagSetting(apiSetting.name(),
                  com.worldventures.dreamtrips.modules.settings.model.Setting.Type.FLAG, (Boolean) apiSetting.value());
         case SELECT:
            return new com.worldventures.dreamtrips.modules.settings.model.SelectSetting(apiSetting.name(),
                  com.worldventures.dreamtrips.modules.settings.model.Setting.Type.SELECT,
                  (String) apiSetting.value(), ((SelectSetting) apiSetting).options());
         default:
            return new com.worldventures.dreamtrips.modules.settings.model.Setting<>("",
                  com.worldventures.dreamtrips.modules.settings.model.Setting.Type.UNKNOWN, "");

      }
   }
}
