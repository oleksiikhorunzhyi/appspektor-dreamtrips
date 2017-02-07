package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SaveEnabledTrackingCommand extends Command<Void> implements InjectableAction {

   @Inject LostCardRepository lostCardRepository;

   private final boolean enable;

   public SaveEnabledTrackingCommand(boolean enable) {
      this.enable = enable;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      lostCardRepository.saveEnabledTracking(enable);
      callback.onSuccess(null);
   }
}
