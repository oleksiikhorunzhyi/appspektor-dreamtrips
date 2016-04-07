package com.messenger.delegate.actions;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;

import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class SaveAvatarAction extends AvatarAction {

    private String avatarPath;

    public SaveAvatarAction(DataConversation conversation,
                            String avatarPath,
                            PhotoUploadingManagerS3 photoUploadingManager,
                            MessengerServerFacade messengerServerFacade,
                            ConversationsDAO conversationsDAO) {
        super(conversation, photoUploadingManager, messengerServerFacade, conversationsDAO);
        this.avatarPath = avatarPath;
    }

    @Override
    protected void run(CommandCallback<DataConversation> callback) {
        uploadAvatar(avatarPath)
                .flatMap(this::sendAvatar)
                .flatMap(this::saveAvatarToDatabase)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
