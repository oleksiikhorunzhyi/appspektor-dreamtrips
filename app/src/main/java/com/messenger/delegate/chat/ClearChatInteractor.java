package com.messenger.delegate.chat;

import com.messenger.delegate.chat.command.ClearChatCommand;
import com.messenger.delegate.chat.command.RevertChatClearingCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class ClearChatInteractor {

    private final ActionPipe<ClearChatCommand> clearChatCommandActionPipe;
    private final ActionPipe<RevertChatClearingCommand> revertChatClearingCommandActionPipe;

    @Inject ClearChatInteractor(Janet janet) {
        clearChatCommandActionPipe = janet.createPipe(ClearChatCommand.class, Schedulers.io());
        revertChatClearingCommandActionPipe = janet.createPipe(RevertChatClearingCommand.class, Schedulers.io());
    }

    public ActionPipe<ClearChatCommand> getClearChatCommandActionPipe() {
        return clearChatCommandActionPipe;
    }

    public ActionPipe<RevertChatClearingCommand> getRevertChatClearingCommandActionPipe() {
        return revertChatClearingCommandActionPipe;
    }
}
