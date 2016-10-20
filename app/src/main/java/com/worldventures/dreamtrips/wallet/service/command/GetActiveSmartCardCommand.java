package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.util.NoActiveSmartCardException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetActiveSmartCardCommand extends Command<SmartCard> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   public GetActiveSmartCardCommand() {
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      String activeSmartCardId = snappyRepository.getActiveSmartCardId();
      if (activeSmartCardId == null) throw new NoActiveSmartCardException("Active Smart Card does not exist.");
      SmartCard smartCard = snappyRepository.getSmartCard(activeSmartCardId);
      if (smartCard == null) throw new NoActiveSmartCardException(String.format("Smart Card with id=%s was not found", activeSmartCardId));
      callback.onSuccess(smartCard);
   }
}
