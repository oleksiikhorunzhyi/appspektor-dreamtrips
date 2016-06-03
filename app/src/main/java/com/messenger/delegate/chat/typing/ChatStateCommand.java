package com.messenger.delegate.chat.typing;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.messengerservers.chat.ChatState;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChatStateCommand extends BaseChatCommand<String> {

    @ChatState.State
    private String chatState;

    public ChatStateCommand(String conversationId, @ChatState.State String chatState) {
        super(conversationId);
        this.chatState = chatState;
    }

    @Override
    protected void run(CommandCallback<String> callback) throws Throwable {
        getChat().flatMap(chat -> chat.setCurrentState(chatState))
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
