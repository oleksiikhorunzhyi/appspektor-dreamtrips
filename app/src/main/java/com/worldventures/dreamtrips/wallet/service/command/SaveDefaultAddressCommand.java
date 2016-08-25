package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SaveDefaultAddressCommand extends Command<AddressInfo> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   private final AddressInfo address;

   public SaveDefaultAddressCommand(AddressInfo address) {
      this.address = address;
   }

   @Override
   protected void run(CommandCallback<AddressInfo> callback) throws Throwable {
      snappyRepository.saveDefaultAddress(address);
      callback.onSuccess(address);
   }
}
