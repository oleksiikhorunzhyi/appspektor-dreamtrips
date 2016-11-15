package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.ClearableStorage;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.Set;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ClearStoragesCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject Set<ActionStorage> storageSet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      for (ActionStorage storage : storageSet)
         if (storage instanceof ClearableStorage) ((ClearableStorage) storage).clearMemory();
   }
}
