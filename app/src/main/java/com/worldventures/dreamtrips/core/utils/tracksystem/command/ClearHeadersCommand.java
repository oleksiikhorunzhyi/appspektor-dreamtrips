package com.worldventures.dreamtrips.core.utils.tracksystem.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.module.AnalyticsModule;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction()
public class ClearHeadersCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(AnalyticsModule.ADOBE_TRACKER) Tracker tracker;

   @Override
   protected void run(CommandCallback<Void> commandCallback) throws Throwable {
      tracker.setHeaderData(null);
      commandCallback.onSuccess(null);
   }
}
