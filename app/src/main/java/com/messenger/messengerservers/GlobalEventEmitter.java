package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.xmpp.UnhandledMessageListener;

import java.util.ArrayList;
import java.util.List;

public abstract class GlobalEventEmitter {

    protected List<Conversation> handledConversations = new ArrayList<>();

    protected List<GlobalMessageListener> globalMessageListeners = new ArrayList<>();
    protected List<InvitationListener> invitationListeners = new ArrayList<>();
    protected List<UnhandledMessageListener> unhandledMessageListeners = new ArrayList<>();

    public void addHandledConversation(Conversation conversation) {
        handledConversations.add(conversation);
    }

    public void removeHandledConversation(Conversation conversation) {
        handledConversations.remove(conversation);
    }

    public void addGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.add(listener);
    }

    public void removeGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.remove(listener);
    }

    protected void notifyGlobalMessage(Message message, boolean incoming) {
        for (GlobalMessageListener listener : globalMessageListeners) {
            if (incoming)
                listener.onReceiveMessage(message);
            else
                listener.onSendMessage(message);
        }
    }

    public void addUnhandledMessageListener(UnhandledMessageListener listener) {
        unhandledMessageListeners.add(listener);
    }

    public void removeUnhandledMessageListener(UnhandledMessageListener listener) {
        unhandledMessageListeners.remove(listener);
    }

    protected void notifyNewUnhandledMessage(Message message) {
        for (UnhandledMessageListener listener : unhandledMessageListeners) {
            listener.onNewUnhandledMessage(message);
        }
    }

    public void addInvitationListener(InvitationListener listener) {
        invitationListeners.add(listener);
    }

    public void removeInvitationListener(InvitationListener listener) {
        invitationListeners.remove(listener);
    }

    protected void notifyReceiveInvite(User userInviter, String roomId, String password) {
        for (InvitationListener listener : invitationListeners) {
            listener.receiveInvite(userInviter, roomId, password);
        }
    }
}
