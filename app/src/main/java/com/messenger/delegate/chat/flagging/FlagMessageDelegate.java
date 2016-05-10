package com.messenger.delegate.chat.flagging;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.CommandActionBase;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FlagMessageDelegate {

    private final ActionPipe<FlagMessageAction> flaggingPipe;

    public FlagMessageDelegate(Janet janet) {
        flaggingPipe = janet.createPipe(FlagMessageAction.class, Schedulers.io());
    }

    public void flagMessage(FlagMessageDTO flagMessageDTO) {
        flaggingPipe.send(new FlagMessageAction(flagMessageDTO));
    }

    public Observable<ActionState<FlagMessageAction>> observeOngoingFlagging() {
        return flaggingPipe.observeWithReplay();
    }
}
