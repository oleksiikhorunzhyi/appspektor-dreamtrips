package com.worldventures.dreamtrips.modules.config.service.command;

import com.worldventures.core.service.command.api_action.MappableApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.config.GetConfigAction;
import com.worldventures.dreamtrips.modules.config.model.Configuration;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadConfigurationCommand extends MappableApiActionCommand<GetConfigAction, Configuration, Configuration> {

   @Override
   protected Class<Configuration> getMappingTargetClass() {
      return Configuration.class;
   }

   @Override
   protected Object mapHttpActionResult(GetConfigAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected GetConfigAction getHttpAction() {
      return new GetConfigAction();
   }

   @Override
   protected Class<GetConfigAction> getHttpActionClass() {
      return GetConfigAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.smth_went_wrong;
   }
}
