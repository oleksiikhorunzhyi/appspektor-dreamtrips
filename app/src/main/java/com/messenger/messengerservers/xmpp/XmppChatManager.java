package com.messenger.messengerservers.xmpp;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.messengerservers.chat.ChatManager;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.xmpp.chats.XmppGroupChat;
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
    public GroupChat createGroupChat(@Nullable String roomId, String ownerId) {
        boolean isOwner = TextUtils.equals(ownerId, facade.getUsername());
        return new XmppGroupChat(facade, roomId, isOwner);
    }

    @Override
    public Observable<GroupChat> createGroupChatObservable(@Nullable String roomId, String ownerId) {
        return Observable.fromCallable(() -> createGroupChat(roomId, ownerId));
    }
}
