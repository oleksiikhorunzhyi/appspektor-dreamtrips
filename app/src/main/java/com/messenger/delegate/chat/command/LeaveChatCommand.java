package com.messenger.delegate.chat.command;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.GroupChat;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LeaveChatCommand extends BaseChatCommand<Chat> {

    public LeaveChatCommand(String conversationId) {
        super(conversationId);
    }

    @Override
    protected void run(CommandCallback<Chat> callback) throws Throwable {
        getChat()
                .map(chat -> (GroupChat) chat)
                .flatMap(GroupChat::leave)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
