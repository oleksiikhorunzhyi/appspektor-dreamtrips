package com.messenger.messengerservers.xmpp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.xmpp.chats.XmppMultiUserChat;
import com.messenger.messengerservers.xmpp.chats.XmppSingleUserChat;

import rx.Observable;

public class XmppChatManager implements ChatManager {
    private final XmppServerFacade facade;

    public XmppChatManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public SingleUserChat createSingleUserChat(@Nullable String companionId, @Nullable String conversationId) {
        return new XmppSingleUserChat(facade, companionId, conversationId);
    }

    @Override
    public MultiUserChat createMultiUserChat(@Nullable String roomId, String ownerId) {
        boolean isOwner = TextUtils.equals(ownerId, facade.getUsername());
        return createMultiUserChat(roomId, ownerId, isOwner);
    }

    @Override
    public MultiUserChat createMultiUserChat(@Nullable String roomId, @NonNull String ownerId, boolean isOwner) {
        return new XmppMultiUserChat(facade, roomId, ownerId, isOwner);
    }

    @Override
    public Observable<MultiUserChat> createMultiUserChatObservable(@Nullable String roomId, String ownerId) {
        return Observable.defer(() -> Observable.just(createMultiUserChat(roomId, ownerId)));
    }
}
