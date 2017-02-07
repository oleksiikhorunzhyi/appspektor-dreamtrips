package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetEnabledTrackingCommand extends Command<Boolean> implements InjectableAction {

   @Inject LostCardRepository lostCardRepository;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      callback.onSuccess(lostCardRepository.isEnableTracking());
   }
}
