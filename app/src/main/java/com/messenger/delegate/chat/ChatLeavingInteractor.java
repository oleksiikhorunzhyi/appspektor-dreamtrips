package com.messenger.delegate.chat;

import com.messenger.delegate.chat.command.LeaveChatCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class ChatLeavingInteractor {

    private final ActionPipe<LeaveChatCommand> leaveChatPipe;

    @Inject
    public ChatLeavingInteractor(Janet janet) {
        leaveChatPipe = janet.createPipe(LeaveChatCommand.class, Schedulers.io());
    }

    public ActionPipe<LeaveChatCommand> getLeaveChatPipe() {
        return leaveChatPipe;
    }
}

