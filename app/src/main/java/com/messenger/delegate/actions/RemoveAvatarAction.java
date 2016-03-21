package com.messenger.delegate.actions;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class RemoveAvatarAction extends AvatarAction {

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
