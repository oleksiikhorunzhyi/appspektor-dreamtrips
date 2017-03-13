package com.worldventures.dreamtrips.modules.version_check.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.version_check.VersionCheckModule;
import com.worldventures.dreamtrips.modules.version_check.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.version_check.model.api.Category;
import com.worldventures.dreamtrips.modules.version_check.model.api.ConfigSetting;
import com.worldventures.dreamtrips.modules.version_check.model.api.UpdateRequirementDTO;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class VersionCheckCommand extends Command<UpdateRequirement> implements InjectableAction, CachedAction<UpdateRequirement> {

   private static final String SCOPE_NAME = "APPLICATION";
   private static final String CATEGORY_NAME = "version";
   private static final String CONFIG_NAME_ANDROID_VERSION = "android_version";
   private static final String CONFIG_NAME_DATE = "date";

   @Inject @Named(VersionCheckModule.JANET_QUALIFIER) Janet janet;
   private UpdateRequirement updateRequirement;

   @Override
   protected void run(CommandCallback<UpdateRequirement> callback) throws Throwable {
      if (updateRequirement != null) callback.onProgress(0);
      janet.createPipe(VersionCheckAction.class)
            .createObservableResult(new VersionCheckAction())
            .map(action -> mapUpdateRequirement(action.getUpdateRequirement()))
            .doOnNext(requirement -> this.updateRequirement = requirement)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private UpdateRequirement mapUpdateRequirement(UpdateRequirementDTO updateRequirementDTO) {
      if (!SCOPE_NAME.equals(updateRequirementDTO.getScope())) {
         throw new IllegalStateException("Scope is not correct");
      }

      Category category = Queryable.from(updateRequirementDTO.getCategories())
            .firstOrDefault(cat -> CATEGORY_NAME.equals(cat.getName()));

      List<ConfigSetting> configSettings = category.getConfigSettings();

      ConfigSetting androidVersion = Queryable.from(configSettings)
            .firstOrDefault(config -> CONFIG_NAME_ANDROID_VERSION.equals(config.getName()));

      ConfigSetting dateConfig = Queryable.from(configSettings)
            .firstOrDefault(config -> CONFIG_NAME_DATE.equals(config.getName()));

      // timestamp defined in seconds on server side
      long timestamp = Long.parseLong(dateConfig.getValue()) * 1000;
      return new UpdateRequirement(androidVersion.getValue(), timestamp);
   }

   @Override
   public UpdateRequirement getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, UpdateRequirement cache) {
      updateRequirement = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }

   public UpdateRequirement getUpdateRequirement() {
      return updateRequirement;
   }
}
