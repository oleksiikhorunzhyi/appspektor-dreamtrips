package com.messenger.delegate.chat.message;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.model.Message;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RetrySendMessageCommand extends BaseChatCommand<Message> {

    @Inject MessageBodyCreator messageBodyCreator;

    private DataMessage failedMessage;

    public RetrySendMessageCommand(DataMessage failedMessage) {
        super(failedMessage.getConversationId());
        this.failedMessage = failedMessage;
    }

    @Override
    protected void run(CommandCallback<Message> callback) throws Throwable {
        Message message = failedMessage.toChatMessage();
        message.setMessageBody(messageBodyCreator.provideForText(failedMessage.getText()));
        getChat().flatMap(chat -> chat.send(message))
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
