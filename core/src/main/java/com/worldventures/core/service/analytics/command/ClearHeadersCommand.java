package com.worldventures.core.service.analytics.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.service.analytics.Tracker;
import com.worldventures.core.service.analytics.TrackerQualifier;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ClearHeadersCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(TrackerQualifier.ADOBE_TRACKER) Tracker tracker;

   @Override
   protected void run(CommandCallback<Void> commandCallback) throws Throwable {
      tracker.setHeaderData(null);
      commandCallback.onSuccess(null);
   }
}
