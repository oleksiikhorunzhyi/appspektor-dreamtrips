package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchDefaultCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject
   SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      callback.onSuccess((BankCard) snappyRepository.readDefaultCard());
   }
}
