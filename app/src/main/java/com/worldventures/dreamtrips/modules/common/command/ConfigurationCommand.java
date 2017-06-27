package com.worldventures.dreamtrips.modules.common.command;

import android.content.res.Configuration;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ConfigurationCommand extends ValueCommandAction<Configuration> {

   public ConfigurationCommand(Configuration value) {
      super(value);
   }
}
