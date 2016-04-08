package com.messenger.delegate;

import android.text.TextUtils;

import com.messenger.delegate.actions.AvatarAction;
import com.messenger.delegate.actions.RemoveAvatarAction;
import com.messenger.delegate.actions.SaveAvatarAction;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConversationAvatarDelegate {

    private PhotoUploadingManagerS3 photoUploadingManager;
    private MessengerServerFacade messengerServerFacade;
    private ConversationsDAO conversationsDAO;

    private Janet janet;
    private ActionPipe<AvatarAction> actionPipe;

    public ConversationAvatarDelegate(PhotoUploadingManagerS3 photoUploadingManager,
                                      MessengerServerFacade messengerServerFacade,
                                      ConversationsDAO conversationsDAO) {
        this.photoUploadingManager = photoUploadingManager;
        this.messengerServerFacade = messengerServerFacade;
        this.conversationsDAO = conversationsDAO;

        janet = new Janet.Builder()
                .addService(new CommandActionService())
                .build();
        actionPipe = janet.createPipe(AvatarAction.class, Schedulers.io());
    }

    public void clearReplays() {
        actionPipe.clearReplays();
    }

    public Observable<ActionState<AvatarAction>> listenToAvatarUpdates(String conversationId) {
        return actionPipe.observeWithReplay()
                .filter(state -> TextUtils.equals(state.action.getConversation().getId(), conversationId))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveAvatar(DataConversation conversation, String avatarPath) {
        actionPipe.send(new SaveAvatarAction(conversation, avatarPath, photoUploadingManager, messengerServerFacade, conversationsDAO));
    }

    public void removeAvatar(DataConversation conversation) {
        actionPipe.send(new RemoveAvatarAction(conversation, photoUploadingManager, messengerServerFacade, conversationsDAO));
    }
}
