package com.messenger.delegate.chat.message;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.messengerservers.model.Message;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChatSendMessageCommand extends BaseChatCommand<Message> {

    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject MessageBodyCreator messageBodyCreator;

    private String messageText;

    public ChatSendMessageCommand(String conversationId, String messageText) {
        super(conversationId);
        this.messageText = messageText;
    }

    @Override
    protected void run(CommandCallback<Message> callback) throws Throwable {
        Message message = new Message.Builder()
                .messageBody(messageBodyCreator.provideForText(messageText))
                .fromId(appSessionHolder.get().get().getUsername())
                .conversationId(conversationId)
                .build();
        getChat().flatMap(chat -> chat.send(message))
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
