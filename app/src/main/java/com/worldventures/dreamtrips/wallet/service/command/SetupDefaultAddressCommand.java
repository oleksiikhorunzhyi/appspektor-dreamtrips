package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SetupDefaultAddressCommand extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   private final AddressInfo addressInfo;

   public SetupDefaultAddressCommand(AddressInfo addressInfo) {
      this.addressInfo = addressInfo;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      snappyRepository.saveDefaultAddress(addressInfo);
      callback.onSuccess(null);
   }
}
