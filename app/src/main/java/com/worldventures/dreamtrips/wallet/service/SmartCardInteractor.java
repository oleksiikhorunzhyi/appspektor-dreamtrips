package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.CardConvertorCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.smartcard.action.support.ConnectAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public final class SmartCardInteractor {
    private final ActionPipe<ConnectAction> connectionPipe;
    private final WriteActionPipe<CardConvertorCommand> fetchListOfCardsPipe;

    @Inject
    public SmartCardInteractor(@Named(JANET_WALLET) Janet janet) {
        connectionPipe = janet.createPipe(ConnectAction.class, Schedulers.io());
        fetchListOfCardsPipe = janet.createPipe(CardConvertorCommand.class, Schedulers.io());
    }

    public ActionPipe<ConnectAction> connectActionPipe() {
        return connectionPipe;
    }

    public WriteActionPipe<CardConvertorCommand> fetchListOfCardsActionPipe() {
        return fetchListOfCardsPipe;
    }
}