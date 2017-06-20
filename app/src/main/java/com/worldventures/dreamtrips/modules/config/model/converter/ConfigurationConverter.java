package com.worldventures.dreamtrips.modules.config.model.converter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.config.model.Category;
import com.worldventures.dreamtrips.api.config.model.ConfigSetting;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.config.model.Configuration;
import com.worldventures.dreamtrips.modules.config.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.config.model.VideoRequirement;

import java.util.List;

import io.techery.mappery.MapperyContext;

public class ConfigurationConverter implements Converter<com.worldventures.dreamtrips.api.config.model.Configuration, Configuration> {

   private static final String SCOPE_NAME = "APPLICATION";
   private static final String CATEGORY_NAME_VERSION = "version";
   private static final String CATEGORY_NAME_VIDEO = "video";

   private static final String CONFIG_NAME_ANDROID_VERSION = "android_version";
   private static final String CONFIG_NAME_ANDROID_DATE = "android_date";
   private static final String CONFIG_NAME_VIDEO_LENGTH = "android_max_selectable_video_duration";

   @Override
   public Configuration convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.config.model.Configuration configuration) {
      if (!SCOPE_NAME.equals(configuration.scope())) {
         throw new IllegalStateException("Scope is not correct");
      }
      return new Configuration(getUpdateRequirement(configuration), getVideoRequirement(configuration));
   }

   private UpdateRequirement getUpdateRequirement(com.worldventures.dreamtrips.api.config.model.Configuration configurationDTO) {
      Category category = getCategoryByName(configurationDTO, CATEGORY_NAME_VERSION);
      List<ConfigSetting> configSettings = category.configSettings();
      ConfigSetting androidVersion = getConfigSettingByName(configSettings, CONFIG_NAME_ANDROID_VERSION);
      ConfigSetting dateConfig = getConfigSettingByName(configSettings, CONFIG_NAME_ANDROID_DATE);
      // timestamp defined in seconds on server side
      long timestamp = Long.parseLong(dateConfig.value()) * 1000;
      return new UpdateRequirement(androidVersion.value(), timestamp);
   }

   private VideoRequirement getVideoRequirement(com.worldventures.dreamtrips.api.config.model.Configuration configurationDTO) {
      Category category = getCategoryByName(configurationDTO, CATEGORY_NAME_VIDEO);
      List<ConfigSetting> configSettings = category.configSettings();
      ConfigSetting videoLength = getConfigSettingByName(configSettings, CONFIG_NAME_VIDEO_LENGTH);
      VideoRequirement videoRequirement = new VideoRequirement();
      videoRequirement.setVideoMaxLength(Integer.parseInt(videoLength.value()));
      return videoRequirement;
   }

   private Category getCategoryByName(com.worldventures.dreamtrips.api.config.model.Configuration configurationDTO, String name) {
      return Queryable.from(configurationDTO.categories()).firstOrDefault(cat -> name.equals(cat.name()));
   }

   private ConfigSetting getConfigSettingByName(List<ConfigSetting> configSettings, String name) {
      return Queryable.from(configSettings).firstOrDefault(config -> name.equals(config.name()));
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.config.model.Configuration> sourceClass() {
      return com.worldventures.dreamtrips.api.config.model.Configuration.class;
   }

   @Override
   public Class<Configuration> targetClass() {
      return Configuration.class;
   }
}
