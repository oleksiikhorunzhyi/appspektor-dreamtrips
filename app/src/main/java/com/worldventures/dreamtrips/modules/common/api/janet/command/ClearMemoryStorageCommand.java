package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class ClearMemoryStorageCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(GetActivitiesCommand.class, Schedulers.io()).send(GetActivitiesCommand.clearMemory());
      janet.createPipe(GetRegionsCommand.class, Schedulers.io()).send(GetRegionsCommand.clearMemory());
   }
}
