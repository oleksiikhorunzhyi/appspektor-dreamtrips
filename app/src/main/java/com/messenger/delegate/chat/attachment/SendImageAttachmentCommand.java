package com.messenger.delegate.chat.attachment;

import android.content.Context;
import android.support.annotation.NonNull;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataPhotoAttachment.PhotoAttachmentStatus;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

import static rx.Observable.just;

@CommandAction
public class SendImageAttachmentCommand extends BaseChatCommand<DataMessage> {
    private final DataMessage message;
    private final DataAttachment attachment;
    private final DataPhotoAttachment photoAttachment;

    @Inject
    UploaderyManager uploaderyManager;
    @Inject
    MessageDAO messageDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    @Inject
    PhotoDAO photoDAO;
    @Inject
    MessageBodyCreator messageBodyCreator;
    @Inject
    @ForApplication
    Context context;

    public SendImageAttachmentCommand(@NonNull String conversationId, @NonNull DataMessage message,
                                      @NonNull DataAttachment attachment,
                                      @NonNull DataPhotoAttachment photoAttachment) {
        super(conversationId);
        this.message = message;
        this.attachment = attachment;
        this.photoAttachment = photoAttachment;
    }

    @Override
    protected void run(CommandCallback<DataMessage> callback) {
        getPhotoUri()
                .flatMap(this::sendMessage)
                .map(m -> message)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<String> getUploadedUriObservable() {
        return just(photoAttachment.getUrl());
    }

    private boolean isUploaded() {
        return photoAttachment.getUploadState() == PhotoAttachmentStatus.UPLOADED;
    }

    private Observable<String> getPhotoUri() {
        return isUploaded() ? getUploadedUriObservable() : getUploadingObservable();
    }

    private Observable<String> getUploadingObservable() {
        return just(photoAttachment.getLocalPath())
                .map(localUri -> UploadingFileManager.copyFileIfNeed(localUri, context))
                .flatMap(uri -> uploaderyManager.getUploadImagePipe()
                        .createObservable(new SimpleUploaderyCommand(uri)))
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
        photoAttachment.setUploadState(PhotoAttachmentStatus.UPLOADING);

        photoDAO.save(photoAttachment);
        attachmentDAO.save(attachment);
        saveMessage(System.currentTimeMillis());
    }

    private void failUploading() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().getMaximum(Calendar.YEAR));
        message.setStatus(MessageStatus.ERROR);
        photoAttachment.setUploadState(PhotoAttachmentStatus.FAILED);
        saveMessage(calendar.getTimeInMillis());
    }

    private void successUploading(String url) {
        message.setStatus(MessageStatus.SENDING);
        photoAttachment.setUploadState(PhotoAttachmentStatus.UPLOADED);
        photoAttachment.setUrl(url);

        photoDAO.save(photoAttachment);
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

        return getChat().flatMap(chat -> chat.send(msg));
    }
}
