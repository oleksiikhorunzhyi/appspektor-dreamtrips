package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class CompressImageInteractor {

    private final ActionPipe<CompressImageForSmartCardCommand> compressImageForSmartCardCommandPipe;

    @Inject
    public CompressImageInteractor(Janet janet) {
        compressImageForSmartCardCommandPipe = janet.createPipe(CompressImageForSmartCardCommand.class, Schedulers.io());
    }

    public ActionPipe<CompressImageForSmartCardCommand> getCompressImageForSmartCardCommandPipe() {
        return compressImageForSmartCardCommandPipe;
    }
}
