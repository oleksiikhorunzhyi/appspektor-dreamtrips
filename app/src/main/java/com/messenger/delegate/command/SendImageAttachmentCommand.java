package com.messenger.delegate.command;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.Utils;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

import static rx.Observable.just;

@CommandAction
public class SendImageAttachmentCommand extends BaseChatAction<DataMessage> {
    private final String filePath;
    private final DataMessage message;
    private final DataAttachment attachment;

    @Inject
    UploaderyManager uploaderyManager;
    @Inject
    MessageDAO messageDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    @Inject
    MessageBodyCreator messageBodyCreator;

    public SendImageAttachmentCommand(DataConversation conversation, @NonNull String filePath,
                                      @NonNull DataMessage message, @NonNull DataAttachment attachment) {
        super(conversation);
        this.filePath = filePath;
        this.message = message;
        this.attachment = attachment;
    }

    @Override
    protected void run(CommandCallback<DataMessage> callback) {
        Observable<String> urlObservable =
                isFile(filePath) ? getUploadingObservable() : just(filePath);

        urlObservable
                .flatMap(this::sendMessage)
                .map(m -> message)
                .subscribe(message -> onSentSuccess(message, callback),
                        throwable -> onSentFail(throwable, callback));
    }

    private boolean isFile(String filePath) {
        return Utils.isFileUri(Uri.parse(filePath));
    }

    private Observable<String> getUploadingObservable() {
        return uploaderyManager.getUploadImagePipe()
                .createObservable(new SimpleUploaderyCommand(filePath))
                .doOnNext(this::handleUploadStatus)
                .compose(new ActionStateToActionTransformer<>())
                .map(action -> ((SimpleUploaderyCommand) action).getResult().getPhotoUploadResponse().getLocation());
    }

    private void handleUploadStatus(ActionState<UploaderyImageCommand> commandActionState) {
        switch (commandActionState.status) {
            case START:
                startUploading();
                break;
            case SUCCESS:
                SimpleUploaderyCommand command = (SimpleUploaderyCommand) commandActionState.action;
                successUploading(command.getResult().getPhotoUploadResponse().getLocation());
                break;
            case FAIL:
                failUploading();
            default:
        }
    }

    private void startUploading() {
        message.setStatus(MessageStatus.SENDING);
        attachment.setUrl(filePath);
        attachmentDAO.save(attachment);
        saveMessage(System.currentTimeMillis());
    }

    private void failUploading() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().getMaximum(Calendar.YEAR));
        message.setStatus(MessageStatus.ERROR);
        saveMessage(calendar.getTimeInMillis());
    }

    private void successUploading(String url) {
        message.setStatus(MessageStatus.SENDING);
        attachment.setUrl(url);
        attachmentDAO.save(attachment);
        saveMessage(System.currentTimeMillis());
    }

    private void saveMessage(long time) {
        message.setDate(new Date(time));
        message.setSyncTime(time);
        messageDAO.save(message);
    }

    private Observable<Message> sendMessage(String fileUrl) {
        Message msg = message.toChatMessage();
        msg.setMessageBody(messageBodyCreator.provideForAttachment(AttachmentHolder
                .newImageAttachment(fileUrl)));

        Chat chat = getChat();
        return chat.send(msg)
                .doOnNext(m -> chat.close());
    }

    private void onSentSuccess(DataMessage message, CommandCallback<DataMessage> callback) {
        message.setStatus(MessageStatus.SENT);
        messageDAO.save(message);
        callback.onSuccess(message);
    }

    private void onSentFail(Throwable throwable, CommandCallback<DataMessage> callback) {
        message.setStatus(MessageStatus.ERROR);
        messageDAO.save(message);
        callback.onFail(throwable);
    }
}
