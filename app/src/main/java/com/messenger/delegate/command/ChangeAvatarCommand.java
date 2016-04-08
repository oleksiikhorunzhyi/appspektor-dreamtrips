package com.messenger.delegate.command;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.storage.dao.ConversationsDAO;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public abstract class ChangeAvatarCommand extends BaseChatAction<DataConversation> {
    @Inject ConversationsDAO conversationsDAO;

    protected ChangeAvatarCommand(DataConversation conversation) {
        super(conversation);
    }

    protected Observable<DataConversation> sendAvatar(String avatar) {
        return createMultiChat()
                .flatMap(multiUserChat -> multiUserChat.setAvatar(avatar))
                .doOnNext(Chat::close)
                .map(chat -> {
                    conversation.setAvatar(avatar);
                    return conversation;
                });
    }

    protected void uploadComplete(DataConversation conversation, CommandCallback<DataConversation> callback) {
        callback.onProgress(100);
        conversationsDAO.save(conversation);
        callback.onSuccess(conversation);
    }
}
