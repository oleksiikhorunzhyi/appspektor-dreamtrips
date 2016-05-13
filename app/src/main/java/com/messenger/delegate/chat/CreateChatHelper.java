package com.messenger.delegate.chat;

import android.support.annotation.NonNull;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.chat.ChatManager;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.ui.helper.ConversationHelper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class CreateChatHelper {

    private ChatManager chatManager;
    private SessionHolder<UserSession> appSessionHolder;

    @Inject
    public CreateChatHelper(MessengerServerFacade messengerServerFacade,
                            SessionHolder<UserSession> appSessionHolder) {
        this.chatManager = messengerServerFacade.getChatManager();
        this.appSessionHolder = appSessionHolder;
    }

    public Observable<Chat> createChat(DataConversation conversation,
                                       @NonNull List<DataUser> participants) {
        switch (conversation.getType()) {
            case ConversationType.CHAT:
                return provideSingleChat(conversation.getId(), participants);
            case ConversationType.GROUP:
            default:
                return provideGroupChat(conversation);
        }
    }


    private Observable<Chat> provideSingleChat(String conversationId, List<DataUser> participants) {
        return Observable.just(participants)
                .filter(usersList -> !usersList.isEmpty())
                .map(users -> users.get(0))
                .map(mate -> chatManager.createSingleUserChat(mate.getId(), conversationId));
    }

    private Observable<Chat> provideGroupChat(DataConversation conversation) {
        DataUser dataUser = new DataUser(appSessionHolder.get().get().getUsername());
        boolean isOwner = ConversationHelper.isOwner(conversation, dataUser);
        return Observable.defer(() -> Observable.just(chatManager.createGroupChat(conversation.getId(),
                dataUser.getId(), isOwner)));
    }
}
