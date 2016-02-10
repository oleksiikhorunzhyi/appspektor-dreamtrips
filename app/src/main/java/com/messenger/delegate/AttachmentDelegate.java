package com.messenger.delegate;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManager;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.util.Date;
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

    public Observable<DataAttachment> prepareMessageWithAttachment(String userId,
                                                                   String conversationId,
                                                                   String filePath) {
        DataMessage dataMessage = new DataMessage.Builder()
                .conversationId(conversationId)
                .from(userId)
                .id(UUID.randomUUID().toString())
                .date(new Date(System.currentTimeMillis()))
                .status(MessageStatus.SENDING)
                .syncTime(System.currentTimeMillis())
                .build();
        //
        return Observable.just(dataMessage)
                .map(message -> prepareAttachment(message, filePath))
                .doOnNext(attachment -> startUpload(dataMessage, attachment));
    }

    private DataAttachment prepareAttachment(DataMessage dataMessage, String filePath) {
        return new DataAttachment.Builder()
                .conversationId(dataMessage.getConversationId())
                .messageId(dataMessage.getId())
                .type(AttachmentType.IMAGE)
                .url(filePath)
                .build();
    }

    private void startUpload(DataMessage dataMessage, DataAttachment dataAttachment) {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(dataAttachment.getUrl());
        TransferObserver transferObserver = photoUploadingManager.upload(uploadTask);
        dataAttachment.setUploadTaskId(transferObserver.getId());
        //
        attachmentDAO.save(dataAttachment);
        messageDAO.save(dataMessage);
    }

    public Observable<DataAttachment> bindToPendingAttachment(DataAttachment dataAttachment) {
        return RxTransferObserver
                .bind(photoUploadingManager.getTransferById(dataAttachment.getUploadTaskId()))
                .doOnError(e -> messageDAO.updateStatus(dataAttachment.getMessageId(), MessageStatus.ERROR, System.currentTimeMillis()))
                .filter(observer -> observer.getState().equals(TransferState.COMPLETED))
                .map(observer -> photoUploadingManager.getResultUrl(observer.getAbsoluteFilePath()))
                .map(originUrl -> {
                    dataAttachment.setUrl(originUrl);
                    dataAttachment.setUploadTaskId(0);
                    attachmentDAO.save(dataAttachment);
                    //
                    return dataAttachment;
                })
              ;
    }

}
