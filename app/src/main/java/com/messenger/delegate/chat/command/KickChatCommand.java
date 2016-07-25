package com.messenger.delegate.chat.command;

import com.messenger.delegate.command.BaseChatCommand;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.storage.dao.ParticipantsDAO;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class KickChatCommand extends BaseChatCommand<Chat> {

    private final String userIdToKick;

    @Inject ParticipantsDAO participantsDAO;

    public KickChatCommand(String conversationId, String userIdToKick) {
        super(conversationId);
        this.userIdToKick = userIdToKick;
    }

    @Override
    protected void run(CommandCallback<Chat> callback) throws Throwable {
        getChat()
                .map(chat -> (GroupChat) chat)
                .flatMap(groupChat -> groupChat.kick(userIdToKick))
                .doOnNext(chat -> participantsDAO.delete(conversationId, userIdToKick))
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
