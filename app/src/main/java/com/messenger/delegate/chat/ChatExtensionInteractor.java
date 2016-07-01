package com.messenger.delegate.chat;

import com.messenger.delegate.chat.command.ClearChatServerCommand;
import com.messenger.delegate.chat.command.RevertClearingChatServerCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class ChatExtensionInteractor {

    private final ActionPipe<ClearChatServerCommand> clearChatServerCommandActionPipe;
    private final ActionPipe<RevertClearingChatServerCommand> revertClearingChatServerCommandActionPipe;

    @Inject ChatExtensionInteractor(Janet janet) {
        clearChatServerCommandActionPipe = janet.createPipe(ClearChatServerCommand.class, Schedulers.io());
        revertClearingChatServerCommandActionPipe = janet.createPipe(RevertClearingChatServerCommand.class, Schedulers.io());
    }

    public ActionPipe<ClearChatServerCommand> getClearChatServerCommandActionPipe() {
        return clearChatServerCommandActionPipe;
    }

    public ActionPipe<RevertClearingChatServerCommand> getRevertClearingChatServerCommandActionPipe() {
        return revertClearingChatServerCommandActionPipe;
    }
}
