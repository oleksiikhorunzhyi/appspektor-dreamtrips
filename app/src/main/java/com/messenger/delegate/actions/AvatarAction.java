package com.messenger.delegate.actions;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.messenger.delegate.RxTransferObserver;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public abstract class AvatarAction extends CommandActionBase<DataConversation> {

    protected DataConversation conversation;

    protected PhotoUploadingManagerS3 photoUploadingManager;
    protected MessengerServerFacade messengerServerFacade;
    protected ConversationsDAO conversationsDAO;

    public AvatarAction(DataConversation conversation,
                        PhotoUploadingManagerS3 photoUploadingManager,
                        MessengerServerFacade messengerServerFacade,
                        ConversationsDAO conversationsDAO) {
        this.conversation = conversation;
        this.photoUploadingManager = photoUploadingManager;
        this.messengerServerFacade = messengerServerFacade;
        this.conversationsDAO = conversationsDAO;
    }

    public DataConversation getConversation() {
        return conversation;
    }

    public Observable<DataConversation> saveAvatarToDatabase(String avatar) {
        return Observable.just(conversation)
                .map(conversation -> {
                    conversation.setAvatar(avatar);
                    conversationsDAO.save(conversation);
                    return conversation;
                });
    }

    public Observable<String> uploadAvatar(String path) {
        UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(path);
        TransferObserver transferObserver = photoUploadingManager.upload(uploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
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

    public Observable<String> sendAvatar(String avatar) {
        return messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversation.getId(), messengerServerFacade.getUsername())
                .flatMap(multiUserChat -> multiUserChat.setAvatar(avatar))
                .doOnNext(multiUserChat -> multiUserChat.close())
                .map(chat -> avatar);
    }
}
