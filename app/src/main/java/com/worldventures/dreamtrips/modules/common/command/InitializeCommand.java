package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.service.Initializable;

import java.util.Set;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class InitializeCommand extends Command<Void> implements InjectableAction {

   @Inject Set<Initializable> initializables;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      try {
         for (Initializable initializable : initializables) {
            initializable.init();
         }
         callback.onSuccess(null);
      } catch (Exception e) {
         callback.onFail(e);
      }
   }
}
