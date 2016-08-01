package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public final class WizardInteractor {
    private WriteActionPipe<CreateAndConnectToCardCommand> createAndConnectPipe;

    @Inject
    public WizardInteractor(@Named(JANET_WALLET) Janet janet) {
        createAndConnectPipe = janet.createPipe(CreateAndConnectToCardCommand.class, Schedulers.io());
    }

    public WriteActionPipe<CreateAndConnectToCardCommand> createAndConnectActionPipe() {
        return createAndConnectPipe;
    }
}