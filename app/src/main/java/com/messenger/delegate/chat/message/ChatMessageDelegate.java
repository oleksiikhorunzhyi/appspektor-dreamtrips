package com.messenger.delegate.chat.message;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Message;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.CommandActionBase;
import io.techery.janet.Janet;
import rx.Observable;
import timber.log.Timber;

public class ChatMessageDelegate {

    private final DataUser currentUser;
    private final MessageBodyCreator messageBodyCreator;
    private final ActionPipe<ChatSendMessageCommand> messageActionPipe;

    @Inject
    public ChatMessageDelegate(Janet janet, DataUser currentUser,
                               MessageBodyCreator messageBodyCreator) {
        this.currentUser = currentUser;
        this.messageBodyCreator = messageBodyCreator;
        this.messageActionPipe = janet.createPipe(ChatSendMessageCommand.class);
    }

    public Observable<Message> sendMessage(String conversationId, String messageText) {
        Message message = new Message.Builder()
                .messageBody(messageBodyCreator.provideForText(messageText))
                .fromId(currentUser.getId())
                .conversationId(conversationId)
                .build();
        return sendMessageInternal(conversationId, message);
    }

    public Observable<Message> retrySendMessage(String conversationId, DataMessage failedMessage) {
        Message message = failedMessage.toChatMessage();
        message.setMessageBody(messageBodyCreator.provideForText(failedMessage.getText()));
        return sendMessageInternal(conversationId, message);
    }

    private Observable<Message> sendMessageInternal(String conversationId, Message message) {
        Observable<Message> messageObservable = messageActionPipe
                .createObservableSuccess(new ChatSendMessageCommand(conversationId, message))
                .map(CommandActionBase::getResult);

        messageObservable.subscribe(resultMessage -> {
        }, e -> Timber.e(e, "Failed to sendMessage"));

        return messageObservable;
    }

}
