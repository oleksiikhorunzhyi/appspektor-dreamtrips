package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupPinCommand;

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
    private final WriteActionPipe<CardListCommand> cardsListPipe;
    private final WriteActionPipe<AttachCardCommand> addRecordPipe;
    private final WriteActionPipe<CardStacksCommand> cardStacksPipe;
    private final ActionPipe<SetupPinCommand> setupPinPipe;
    private final ActionPipe<GetSmartCardCommand> getSmartCardPipe;
    private final ActionPipe<GetActiveSmartCardCommand> getActiveSmartCardPipe;

    @Inject
    public SmartCardInteractor(@Named(JANET_WALLET) Janet janet) {
        connectionPipe = janet.createPipe(ConnectAction.class, Schedulers.io());
        cardsListPipe = janet.createPipe(CardListCommand.class, Schedulers.io());
        addRecordPipe = janet.createPipe(AttachCardCommand.class, Schedulers.io());
        cardStacksPipe = janet.createPipe(CardStacksCommand.class, Schedulers.io());
        setupPinPipe = janet.createPipe(SetupPinCommand.class, Schedulers.io());
        getSmartCardPipe = janet.createPipe(GetSmartCardCommand.class, Schedulers.io());
        getActiveSmartCardPipe = janet.createPipe(GetActiveSmartCardCommand.class, Schedulers.io());
    }

    public ActionPipe<ConnectAction> connectActionPipe() {
        return connectionPipe;
    }

    public WriteActionPipe<CardListCommand> cardsListPipe() {
        return cardsListPipe;
    }

    public WriteActionPipe<AttachCardCommand> addRecordPipe() {
        return addRecordPipe;
    }

    public WriteActionPipe<CardStacksCommand> cardStacksPipe() {
        return cardStacksPipe;
    }

    public ActionPipe<SetupPinCommand> setupPinPipe() {
        return setupPinPipe;
    }

    public ActionPipe<GetSmartCardCommand> getSmartCardPipe() {
        return getSmartCardPipe;
    }

    public ActionPipe<GetActiveSmartCardCommand> getActiveSmartCardPipe() {
        return getActiveSmartCardPipe;
    }
}