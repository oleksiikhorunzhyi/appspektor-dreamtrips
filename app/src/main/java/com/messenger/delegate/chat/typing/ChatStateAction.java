package com.messenger.delegate.chat.typing;

import com.messenger.delegate.command.BaseChatAction;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.ChatState;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChatStateAction extends BaseChatAction<String> {

    @ChatState.State
    private String chatState;

    public ChatStateAction(String conversationId, @ChatState.State String chatState) {
        super(conversationId);
        this.chatState = chatState;
    }

    @Override
    protected void run(CommandCallback<String> callback) throws Throwable {
        getChat().flatMap(chat -> chat.setCurrentState(chatState))
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
