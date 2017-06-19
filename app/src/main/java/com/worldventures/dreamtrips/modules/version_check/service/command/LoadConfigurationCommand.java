package com.worldventures.dreamtrips.modules.version_check.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.version_check.VersionCheckModule;
import com.worldventures.dreamtrips.modules.version_check.model.Configuration;
import com.worldventures.dreamtrips.modules.version_check.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.version_check.model.VideoRequirement;
import com.worldventures.dreamtrips.modules.version_check.model.api.Category;
import com.worldventures.dreamtrips.modules.version_check.model.api.ConfigSetting;
import com.worldventures.dreamtrips.modules.version_check.model.api.ConfigurationDTO;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadConfigurationCommand extends Command<Configuration> implements InjectableAction {

   private static final String SCOPE_NAME = "APPLICATION";
   private static final String CATEGORY_NAME_VERSION = "version";
   private static final String CATEGORY_NAME_VIDEO = "video";

   private static final String CONFIG_NAME_ANDROID_VERSION = "android_version";
   private static final String CONFIG_NAME_ANDROID_DATE = "android_date";
   private static final String CONFIG_NAME_VIDEO_LENGTH = "android_date";

   @Inject @Named(VersionCheckModule.JANET_QUALIFIER) Janet janet;

   @Override
   protected void run(CommandCallback<Configuration> callback) throws Throwable {
      janet.createPipe(VersionCheckAction.class)
            .createObservableResult(new VersionCheckAction())
            .map(action -> mapConfiguration(action.getConfiguration()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Configuration mapConfiguration(ConfigurationDTO configurationDTO) {
      if (!SCOPE_NAME.equals(configurationDTO.getScope())) {
         throw new IllegalStateException("Scope is not correct");
      }
      return new Configuration(getUpdateRequirement(configurationDTO), getVideoRequrement(configurationDTO));
   }

   private UpdateRequirement getUpdateRequirement(ConfigurationDTO configurationDTO) {
      Category category = getCategoryByName(configurationDTO, CATEGORY_NAME_VERSION);
      List<ConfigSetting> configSettings = category.getConfigSettings();
      ConfigSetting androidVersion = getConfigSettingByName(configSettings, CONFIG_NAME_ANDROID_VERSION);
      ConfigSetting dateConfig = getConfigSettingByName(configSettings, CONFIG_NAME_ANDROID_DATE);
      // timestamp defined in seconds on server side
      long timestamp = Long.parseLong(dateConfig.getValue()) * 1000;
      return new UpdateRequirement(androidVersion.getValue(), timestamp);
   }

   private VideoRequirement getVideoRequrement(ConfigurationDTO configurationDTO) {
      Category category = getCategoryByName(configurationDTO, CATEGORY_NAME_VIDEO);
      List<ConfigSetting> configSettings = category.getConfigSettings();
      ConfigSetting videoLength = getConfigSettingByName(configSettings, CONFIG_NAME_VIDEO_LENGTH);
      VideoRequirement videoRequirement = new VideoRequirement();
      videoRequirement.setVideoMaxLength(Integer.parseInt(videoLength.getValue()));
      return videoRequirement;
   }

   private Category getCategoryByName(ConfigurationDTO configurationDTO, String name) {
      return Queryable.from(configurationDTO.getCategories()).firstOrDefault(cat -> name.equals(cat.getName()));
   }

   private ConfigSetting getConfigSettingByName(List<ConfigSetting> configSettings, String name) {
      return Queryable.from(configSettings).firstOrDefault(config -> name.equals(config.getName()));
   }
}
