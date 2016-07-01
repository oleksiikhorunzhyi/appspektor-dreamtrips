package com.messenger.delegate.chat.command;

import com.messenger.messengerservers.ChatExtensions;
import com.messenger.messengerservers.event.RevertClearingEvent;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RevertClearingChatServerCommand extends Command<RevertClearingEvent> {

    private final String conversationId;

    @Inject ChatExtensions chatExtensions;

    public RevertClearingChatServerCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    protected void run(CommandCallback<RevertClearingEvent> callback) throws Throwable {
        chatExtensions
                .revertChatClearing(conversationId)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
