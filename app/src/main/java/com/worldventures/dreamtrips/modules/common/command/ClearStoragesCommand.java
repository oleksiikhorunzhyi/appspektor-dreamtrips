package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.ClearableStorage;
import com.worldventures.janet.cache.storage.Storage;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;

import java.util.Set;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ClearStoragesCommand extends Command<Void> implements InjectableAction {

   @Inject Set<ActionStorage> storageSet;
   @Inject Set<MultipleActionStorage> multipleActionStorageSet;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      cleanStorageSet(storageSet);
      cleanStorageSet(multipleActionStorageSet);
   }

   private void cleanStorageSet(Set<? extends Storage> storageSet) {
      for (Storage storage : storageSet) {
         if (storage instanceof ClearableStorage) {
            ((ClearableStorage) storage).clearMemory();
         }
      }
   }
}
