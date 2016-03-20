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

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.CommandActionBase;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.ReadOnlyActionPipe;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class ConversationAvatarDelegate {

    private PhotoUploadingManagerS3 photoUploadingManager;
    private MessengerServerFacade messengerServerFacade;
    private ConversationsDAO conversationsDAO;

    private Janet janet;
    private ActionPipe<BaseAvatarAction> actionPipe;

    public ConversationAvatarDelegate(PhotoUploadingManagerS3 photoUploadingManager,
                                      MessengerServerFacade messengerServerFacade,
                                      ConversationsDAO conversationsDAO) {
        this.photoUploadingManager = photoUploadingManager;
        this.messengerServerFacade = messengerServerFacade;
        this.conversationsDAO = conversationsDAO;

        janet = new Janet.Builder()
                .addService(new CommandActionService())
                .build();
        actionPipe = janet.createPipe(BaseAvatarAction.class);
    }

    public Observable<ActionState<BaseAvatarAction>> listenToAvatarUpdates(DataConversation dataConversation) {
        ReadOnlyActionPipe<BaseAvatarAction> avatarPipe = actionPipe
                .filter(action -> TextUtils.equals(action.getConversation().getId(), dataConversation.getId()));
        Observable<ActionState<BaseAvatarAction>> observable = avatarPipe.observe().publish().refCount();
        // Is there any way to improve this so that we don't have to use publish()?
        observable.subscribe((new ActionStateSubscriber<BaseAvatarAction>()
            .onFail((avatarAction, e) -> {
                DataConversation c = avatarAction.getConversation();
                Timber.e(e, "Failed to update conversation avatar, setting to null");
                c.setAvatar(null);
                conversationsDAO.save(c);
            })));
        return observable;
    }

    public void saveAvatar(DataConversation conversation) {
        actionPipe.send(new SaveAvatarAction(conversation, photoUploadingManager, messengerServerFacade, conversationsDAO));
    }

    public void removeAvatar(DataConversation conversation) {
        actionPipe.send(new RemoveAvatarAction(conversation, photoUploadingManager, messengerServerFacade, conversationsDAO));
    }

    @CommandAction
    public static class SaveAvatarAction extends BaseAvatarAction {

        public SaveAvatarAction(DataConversation conversation,
                                PhotoUploadingManagerS3 photoUploadingManager,
                                MessengerServerFacade messengerServerFacade,
                                ConversationsDAO conversationsDAO) {
            super(conversation, photoUploadingManager, messengerServerFacade, conversationsDAO);
        }

        @Override
        protected void run(CommandCallback<DataConversation> callback) {
            saveAvatarToDatabase(conversation)
                    .flatMap(c -> uploadAvatar(conversation.getAvatar()))
                    .map(url -> {
                        conversation.setAvatar(url);
                        return conversation;
                    })
                    .flatMap(this::sendAvatar)
                    .flatMap(this::saveAvatarToDatabase)
                    .compose(new IoToMainComposer<>())
                    .subscribe(callback::onSuccess, callback::onFail);
        }
    }

    @CommandAction
    public static class RemoveAvatarAction extends BaseAvatarAction {

        public RemoveAvatarAction(DataConversation conversation,
                                PhotoUploadingManagerS3 photoUploadingManager,
                                MessengerServerFacade messengerServerFacade,
                                ConversationsDAO conversationsDAO) {
            super(conversation, photoUploadingManager, messengerServerFacade, conversationsDAO);
        }

        @Override
        protected void run(CommandCallback<DataConversation> callback) {
            Observable.just(conversation)
                    .map(c -> {
                        c.setAvatar(null);
                        return c;
                    })
                    .flatMap(c -> sendAvatar(c)
                    .flatMap(this::saveAvatarToDatabase)
                    .compose(new IoToMainComposer<>()))
                    .subscribe(callback::onSuccess, callback::onFail);
        }
    }

    @CommandAction
    public static abstract class BaseAvatarAction extends CommandActionBase<DataConversation> {

        protected DataConversation conversation;
        protected PhotoUploadingManagerS3 photoUploadingManager;
        protected MessengerServerFacade messengerServerFacade;
        protected ConversationsDAO conversationsDAO;

        public BaseAvatarAction(DataConversation conversation,
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

        public Observable<DataConversation> sendAvatar(DataConversation conversation) {
            return messengerServerFacade.getChatManager()
                    .createMultiUserChatObservable(conversation.getId(), messengerServerFacade.getUsername())
                    .flatMap(multiUserChat -> multiUserChat.setAvatar(conversation.getAvatar()))
                    .doOnNext(multiUserChat -> multiUserChat.close())
                    .map(multiUserChat1 -> conversation);
        }
    }
}
