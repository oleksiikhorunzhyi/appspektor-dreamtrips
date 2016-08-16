package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetDefaultAddressCommand extends Command<AddressInfo> implements InjectableAction {

    @Inject
    SnappyRepository snappyRepository;

    @Override
    protected void run(CommandCallback<AddressInfo> callback) throws Throwable {
        callback.onSuccess(snappyRepository.readDefaultAddress());
    }
}
