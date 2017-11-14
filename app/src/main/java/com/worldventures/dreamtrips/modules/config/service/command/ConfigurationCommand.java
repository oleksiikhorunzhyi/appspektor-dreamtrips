package com.worldventures.dreamtrips.modules.config.service.command;

import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.config.model.Configuration;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ConfigurationCommand extends Command<Configuration> implements CachedAction<Configuration> {

   private Configuration configuration;

   public ConfigurationCommand() {
      //do nothing
   }

   public ConfigurationCommand(Configuration configuration) {
      this.configuration = configuration;
   }

   @Override
   protected void run(CommandCallback<Configuration> callback) throws Throwable {
      if (configuration == null) {
         configuration = new Configuration();
      }
      callback.onSuccess(configuration);
   }

   @Override
   public Configuration getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, Configuration cache) {
      if (configuration == null) {
         configuration = cache;
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .build();
   }

   public int getVideoMaxLength() {
      return configuration.getVideoRequirement().getVideoMaxLength();
   }
}
