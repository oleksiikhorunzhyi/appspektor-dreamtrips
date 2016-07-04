package com.messenger.delegate.chat.event;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class ChatEventInteractor {
    private final ActionPipe<ClearChatCommand> eventClearChatPipe;
    private final ActionPipe<RevertClearingChatCommand> eventRevertClearingChatPipe;

    @Inject ChatEventInteractor(Janet janet) {
        eventClearChatPipe = janet.createPipe(ClearChatCommand.class, Schedulers.io());
        eventRevertClearingChatPipe = janet.createPipe(RevertClearingChatCommand.class, Schedulers.io());
    }

    public ActionPipe<ClearChatCommand> getEventClearChatPipe() {
        return eventClearChatPipe;
    }

    public ActionPipe<RevertClearingChatCommand> getEventRevertClearingChatPipe() {
        return eventRevertClearingChatPipe;
    }
}
