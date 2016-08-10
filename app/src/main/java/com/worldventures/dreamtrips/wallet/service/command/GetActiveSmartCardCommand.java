package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetActiveSmartCardCommand  extends Command<SmartCard> implements InjectableAction {

    @Inject SnappyRepository snappyRepository;

    public GetActiveSmartCardCommand() {
    }

    @Override
    protected void run(CommandCallback<SmartCard> callback) throws Throwable {
        // TODO update info from SC device with set of actions
        callback.onSuccess(snappyRepository.getSmartCard(snappyRepository.getActiveSmartCardId()));
    }
}
