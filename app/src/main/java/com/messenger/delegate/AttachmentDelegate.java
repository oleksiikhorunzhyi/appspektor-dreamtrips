package com.messenger.delegate;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
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
import rx.Subscriber;

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
                .map(msg -> {
                    AttachmentHolder holder = AttachmentHolder.newImageAttachment(imagePath);
                    MessageBody messageBody = msg.getMessageBody();
                    if (messageBody == null) msg.setMessageBody(messageBody = new MessageBody());
                    messageBody.setAttachments(Collections.singletonList(holder));
                    return holder;
                })
                .flatMap(attachmentHolder -> {
                    message.setId(UUID.randomUUID().toString());
                    message.setDate(System.currentTimeMillis());
                    final DataAttachment attachment = new DataAttachment(attachmentHolder, message.getId(), 0);
                    attachmentDAO.save(attachment);
                    messageDAO.save(new DataMessage(message));

                    final UploadTask uploadTask = new UploadTask();
                    uploadTask.setFilePath(imagePath);
                    return RxTransferObserver.bind(photoUploadingManager.upload(uploadTask))
                            .map(aVoid -> {
                                String remoteUrl = photoUploadingManager.getResultUrl(uploadTask);
                                attachment.setUrl(remoteUrl);
                                // noinspection all
                                ((ImageAttachment) message.getMessageBody()
                                        .getAttachments().get(0).getItem()).setUrl(remoteUrl);

                                attachmentDAO.save(attachment);
                                return message;
                            });
                });
    }

    public static class RxTransferObserver implements Observable.OnSubscribe<Void> {
        private final TransferObserver uploadObservable;

        private RxTransferObserver(TransferObserver uploadObservable) {
            this.uploadObservable = uploadObservable;
        }

        public static Observable<Void> bind(TransferObserver uploadObservable)  {
            return Observable.create(new RxTransferObserver(uploadObservable));
        }

        @Override
        public void call(Subscriber<? super Void> subscriber) {
            uploadObservable.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        subscriber.onNext((Void) null);
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    subscriber.onError(ex);
                }
            });
        }
    }
}
