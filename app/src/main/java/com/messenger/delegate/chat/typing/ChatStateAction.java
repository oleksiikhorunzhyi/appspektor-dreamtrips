package com.messenger.delegate.chat.typing;

import com.messenger.delegate.command.BaseChatAction;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.ChatState;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChatStateAction extends BaseChatAction<String> {

    @ChatState.State
    private String chatState;

    public ChatStateAction(DataConversation conversation, @ChatState.State String chatState) {
        super(conversation);
        this.chatState = chatState;
    }

    @Override
    protected void run(CommandCallback<String> callback) throws Throwable {
        getChat().setCurrentState(chatState)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
