package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CardCountCommand extends Command<Integer> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   public CardCountCommand() {
   }

   @Override
   protected void run(CommandCallback<Integer> callback) throws Throwable {
      List cardList = snappyRepository.readWalletCardsList();
      callback.onSuccess(cardList == null ? 0 : cardList.size());
   }
}
