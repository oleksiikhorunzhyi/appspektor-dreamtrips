package com.messenger.delegate.command;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.GroupChat;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public abstract class ChangeAvatarCommand extends BaseChatAction<DataConversation> {
    protected ChangeAvatarCommand(String conversationId) {
        super(conversationId);
    }

    protected Observable<DataConversation> sendAvatar(String avatar) {
        return getChat()
                .map(chat -> (GroupChat) chat)
                .flatMap(multiUserChat -> multiUserChat.setAvatar(avatar))
                .flatMap(chat -> conversationsDAO.getConversation(conversationId))
                .take(1)
                .map(dataConversation -> {
                    dataConversation.setAvatar(avatar);
                    return dataConversation;
                });
    }

    protected void uploadComplete(DataConversation conversation, CommandCallback<DataConversation> callback) {
        callback.onProgress(100);
        conversationsDAO.save(conversation);
        callback.onSuccess(conversation);
    }
}
