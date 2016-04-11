package com.messenger.delegate.command;

import android.support.annotation.NonNull;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.MessageDAO;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SendLocationAttachmentCommand extends BaseChatAction<DataMessage> {
    private final DataMessage message;
    private final DataAttachment attachment;
    private final DataLocationAttachment locationAttachment;

    @Inject
    MessageDAO messageDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    @Inject
    LocationDAO locationDAO;
    @Inject
    MessageBodyCreator messageBodyCreator;

    public SendLocationAttachmentCommand(DataConversation conversation, @NonNull DataMessage message,
                                      @NonNull DataAttachment attachment, @NonNull DataLocationAttachment locationAttachment) {
        super(conversation);
        this.message = message;
        this.attachment = attachment;
        this.locationAttachment = locationAttachment;
    }

    @Override
    protected void run(CommandActionBase.CommandCallback<DataMessage> callback) {
        startSending();
        sendLocationMessage(locationAttachment.getLat(), locationAttachment.getLng())
                .map(msg -> message)
                .subscribe(message -> onSentSuccess(message, callback),
                        throwable -> onSentFail(throwable, callback));
    }

    private void startSending() {
        message.setStatus(MessageStatus.SENDING);

        attachmentDAO.save(attachment);
        locationDAO.save(locationAttachment);
        saveMessage(System.currentTimeMillis());
    }

    private void saveMessage(long time) {
        message.setDate(new Date(time));
        message.setSyncTime(time);
        messageDAO.save(message);
    }

    private Observable<Message> sendLocationMessage(double lat, double lng) {
        Message msg = message.toChatMessage();
        msg.setMessageBody(messageBodyCreator.provideForAttachment(AttachmentHolder
                .newLocationAttachment(lat, lng)));

        Chat chat = getChat();
        return chat.send(msg)
                .doOnNext(m -> chat.close());
    }

    private void onSentSuccess(DataMessage message, CommandActionBase.CommandCallback<DataMessage> callback) {
        message.setStatus(MessageStatus.SENT);
        messageDAO.save(message);
        callback.onSuccess(message);
    }

    private void onSentFail(Throwable throwable, CommandActionBase.CommandCallback<DataMessage> callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().getMaximum(Calendar.YEAR));
        message.setStatus(MessageStatus.ERROR);
        saveMessage(calendar.getTimeInMillis());
        callback.onFail(throwable);
    }
}
