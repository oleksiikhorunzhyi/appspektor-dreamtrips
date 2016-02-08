package com.messenger.delegate;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.messenger.delegate.RxTransferObserver.UploadProblemException;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.ImageAttachment;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManager;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.util.Collections;
import java.util.UUID;

import rx.Observable;

public class AttachmentDelegate {
    private final PhotoUploadingManager photoUploadingManager;
    private final MessageDAO messageDAO;
    private final AttachmentDAO attachmentDAO;

    public AttachmentDelegate(PhotoUploadingManager photoUploadingManager,
                              MessageDAO messageDAO, AttachmentDAO attachmentDAO) {
        this.photoUploadingManager = photoUploadingManager;
        this.messageDAO = messageDAO;
        this.attachmentDAO = attachmentDAO;
    }

    /*
    * 1. Create message with attachment
    *
    * 2. Save message and attachment in db
    *
    * 3. Upload image on amazon
    *
    * 4. Update attachment in db
    *
    * 5. Send message with attachment
    * */
    public Observable<Message> prepareMessageWithAttachment(Message message, String imagePath) {
        return Observable.just(message)
                .map(msg -> createAttachmentAndAttachToMessage(msg, imagePath))
                .flatMap(attachmentHolder -> {
                    message.setId(UUID.randomUUID().toString());
                    message.setDate(System.currentTimeMillis());
                    final DataAttachment attachment = new DataAttachment(attachmentHolder, message.getId(), 0);
                    save(attachment, new DataMessage(message));

                    return uploadImageForMessage(message, imagePath);
                });
    }

    private void save(DataAttachment attachment, DataMessage message) {
        attachmentDAO.save(attachment);
        messageDAO.save(message);
    }

    private Observable<Message> uploadImageForMessage(Message message, String imagePath) {
        final UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(imagePath);
        TransferObserver transferObserver = photoUploadingManager.upload(uploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
        return RxTransferObserver.bind(transferObserver)
                .doOnError(throwable -> {
                    if (throwable instanceof UploadProblemException) {
                        photoUploadingManager.cancelUploading(uploadTask);
                    }
                })
                .map(aVoid -> setRemoteImageUrl(message, photoUploadingManager.getResultUrl(uploadTask)));
    }

    private AttachmentHolder createAttachmentAndAttachToMessage(Message message, String imagePath) {
        AttachmentHolder holder = AttachmentHolder.newImageAttachment(imagePath);
        MessageBody messageBody = message.getMessageBody();
        if (messageBody == null) message.setMessageBody(messageBody = new MessageBody());
        messageBody.setAttachments(Collections.singletonList(holder));
        return holder;
    }

    private Message setRemoteImageUrl(Message message, String remoteUrl) {
        // noinspection all
        ((ImageAttachment) message.getMessageBody()
                .getAttachments().get(0).getItem()).setOriginUrl(remoteUrl);
        return message;
    }
}
