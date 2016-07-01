package com.messenger.delegate.chat.command;

import com.messenger.messengerservers.ChatExtensions;
import com.messenger.storage.dao.ConversationsDAO;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class RevertChatClearingCommand extends Command<Void> {

    private final String conversationId;

    @Inject ChatExtensions chatExtensions;
    @Inject ConversationsDAO conversationsDAO;

    public RevertChatClearingCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        chatExtensions
                .revertChatClearing(conversationId)
                .doOnNext(event -> Timber.d("REVERT CLEARING %s", event))
                .doOnNext(event -> conversationsDAO.setClearDate(event.getConversationId(), 0))
                .map(event -> (Void) null)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
