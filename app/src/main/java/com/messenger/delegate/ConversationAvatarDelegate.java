package com.messenger.delegate;

import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ConversationAvatarDelegate {

    private PhotoUploadingManagerS3 photoUploadingManager;
    private MessengerServerFacade messengerServerFacade;
    private ConversationsDAO conversationsDAO;

    private PublishSubject<DataConversation> conversationsStream = PublishSubject.create();

    public ConversationAvatarDelegate(PhotoUploadingManagerS3 photoUploadingManager,
                                      MessengerServerFacade messengerServerFacade,
                                      ConversationsDAO conversationsDAO) {
        this.photoUploadingManager = photoUploadingManager;
        this.messengerServerFacade = messengerServerFacade;
        this.conversationsDAO = conversationsDAO;
    }

    public Observable<DataConversation> listenToAvatarUpdates(DataConversation dataConversation) {
        return conversationsStream.filter(c -> TextUtils.equals(c.getId(), dataConversation.getId()));
    }

    public void saveAvatar(DataConversation conversation) {
        saveAvatarToDatabase(conversation)
            .flatMap(c -> uploadAvatar(conversation.getAvatar()))
            .map(url -> {
                conversation.setAvatar(url);
                return conversation;
            })
            .flatMap(this::sendAvatar)
            .flatMap(this::saveAvatarToDatabase)
            .compose(new IoToMainComposer<>())
            .subscribe(conversationsStream);
    }

    public void removeAvatar(DataConversation conversation) {
        Observable.just(conversation)
            .map(c -> {
                c.setAvatar(null);
                return c;
            })
            .flatMap(this::sendAvatar)
            .flatMap(this::saveAvatarToDatabase)
            .compose(new IoToMainComposer<>())
            .subscribe(conversationsStream);
    }

    public Observable<DataConversation> saveAvatarToDatabase(DataConversation dataConversation) {
        return Observable.just(dataConversation)
                .map(conversation -> {
                    conversationsDAO.save(conversation);
                    return conversation;
                });
    }

    public Observable<String> uploadAvatar(String path) {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(path);
        TransferObserver transferObserver = photoUploadingManager.upload(uploadTask);
        return RxTransferObserver.bind(transferObserver)
                .filter(observer -> processTransferState(transferObserver, uploadTask))
                .map(observer -> photoUploadingManager.getResultUrl(observer.getAbsoluteFilePath()));
    }

    private boolean processTransferState(TransferObserver observer, UploadTask uploadTask) {
        if (observer.getState().equals(TransferState.FAILED)
                || observer.getState().equals(TransferState.WAITING_FOR_NETWORK)) {
            photoUploadingManager.cancelUploading(uploadTask);
            throw new RuntimeException("Failed uploading avatar");
        }
        return observer.getState().equals(TransferState.COMPLETED);
    }

    public Observable<DataConversation> sendAvatar(DataConversation conversation) {
        return messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversation.getId(), messengerServerFacade.getUsername())
                .flatMap(multiUserChat -> multiUserChat.setAvatar(conversation.getAvatar()))
                .doOnNext(multiUserChat -> multiUserChat.close())
                .map(multiUserChat1 -> conversation);
    }
}
