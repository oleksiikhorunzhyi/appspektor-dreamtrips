package com.messenger.util;


import com.messenger.delegate.ChatMessagesEventDelegate;
import com.messenger.delegate.GroupChatEventDelegate;
import com.messenger.delegate.JoinedChatEventDelegate;
import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.model.Message;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

import rx.Observable;

public class ChatFacadeManager {

    @Inject
    ChatMessagesEventDelegate chatMessagesDelegate;
    @Inject
    GroupChatEventDelegate groupChatEventDelegate;
    @Inject
    JoinedChatEventDelegate joinedChatDelegate;

    public ChatFacadeManager(Injector injector) {
        injector.inject(this);
    }

    public void onReceivedMessage(Message message){
        chatMessagesDelegate.onReceivedMessage(message);
    }

    public void onPreSendMessage(Message message){
        chatMessagesDelegate.onPreSendMessage(message);
    }

    public void onSendMessage(Message message) {
        chatMessagesDelegate.onSendMessage(message);
    }

    public void onErrorMessage(Message message) {
        chatMessagesDelegate.onErrorMessage(message);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onChatInvited(String conversationId){
        groupChatEventDelegate.onChatInvited(conversationId);
    }

    public void onSubjectChanged(String conversationId, String subject){
        groupChatEventDelegate.onSubjectChanged(conversationId, subject);
    }

    public void onAvatarChanged(String conversationId, String avatar) {
        groupChatEventDelegate.onAvatarChanged(conversationId, avatar);
    }

    public void onChatLeft(String conversationId, String userId, boolean leave){
        groupChatEventDelegate.onChatLeft(conversationId, userId, leave);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void processJoinedEvents(Observable<JoinedEvent> joinedEventObservable) {
        joinedChatDelegate.processJoinedEvents(joinedEventObservable);
    }

}
