package com.worldventures.dreamtrips.wallet.service;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public final class WizardInteractor {
    private WriteActionPipe<Void> provisionPipe;

    @Inject
    public WizardInteractor(@Named(JANET_WALLET) Janet janet) {
        provisionPipe = janet.createPipe(Void.class);
    }

    public WriteActionPipe<Void> provisionPipe() {
        return provisionPipe;
    }
}