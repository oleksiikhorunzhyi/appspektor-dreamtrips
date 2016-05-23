package com.messenger.delegate.chat.message;

import com.messenger.delegate.command.BaseChatAction;
import com.messenger.messengerservers.model.Message;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChatSendMessageAction extends BaseChatAction<Message> {

    private Message message;

    public ChatSendMessageAction(String conversationId, Message message) {
        super(conversationId);
        this.message = message;
    }

    @Override

    protected void run(CommandCallback<Message> callback) throws Throwable {
        getChat().flatMap(chat -> chat.send(message))
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
