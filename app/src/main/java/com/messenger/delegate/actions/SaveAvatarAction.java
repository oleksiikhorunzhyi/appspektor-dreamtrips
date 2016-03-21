package com.messenger.delegate.actions;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class SaveAvatarAction extends AvatarAction {

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
                .doOnError(e -> {
                    Timber.e(e, "Group avatar - failed to update conversation avatar, setting to null");
                    conversation.setAvatar(null);
                    conversationsDAO.save(conversation);
                })
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
