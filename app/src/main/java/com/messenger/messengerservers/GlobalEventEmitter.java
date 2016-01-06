package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.listeners.OnChatCreatedListener;
import com.messenger.messengerservers.listeners.OnLeftChatListener;
import com.messenger.messengerservers.listeners.OnSubjectChangedListener;
import com.messenger.messengerservers.listeners.PresenceListener;
import com.messenger.messengerservers.xmpp.UnhandledMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GlobalEventEmitter {

    protected List<Conversation> handledConversations = new ArrayList<>();

    protected List<GlobalMessageListener> globalMessageListeners = new ArrayList<>();
    protected List<PresenceListener> presenceListeners = new ArrayList<>();
    protected List<InvitationListener> invitationListeners = new ArrayList<>();
    protected List<UnhandledMessageListener> unhandledMessageListeners = new ArrayList<>();
    protected List<OnSubjectChangedListener> onSubjectChangedListeners = new CopyOnWriteArrayList<>();
    protected List<OnLeftChatListener> onLeftChatListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatCreatedListener> onChatCreatedListeners = new CopyOnWriteArrayList<>();

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

    public void addPresenceListener(PresenceListener listener) {
        presenceListeners.add(listener);
    }

    public void removePresenceListener(PresenceListener listener) {
        presenceListeners.remove(listener);
    }

    protected void notifyUserPresenceChanged(String userId, boolean isOnline) {
        for (PresenceListener listener: presenceListeners) {
            listener.onUserPresenceChanged(userId, isOnline);
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

    public void addOnSubjectChangesListener(OnSubjectChangedListener listener) {
        onSubjectChangedListeners.add(listener);
    }

    public void removeOnSubjectChangesListener(OnSubjectChangedListener listener) {
        onSubjectChangedListeners.remove(listener);
    }

    protected void notifyOnSubjectChanges(String conversationId, String subject) {
        for (OnSubjectChangedListener listener : onSubjectChangedListeners) {
            listener.onSubjectChanged(conversationId, subject);
        }
    }

    public void addOnLeftChatListener(OnLeftChatListener listener) {
        onLeftChatListeners.add(listener);
    }

    public void removeOnLeftChatListener(OnLeftChatListener listener) {
        onLeftChatListeners.remove(listener);
    }

    protected void notifyOnLeftChatListener(String conversationId, String userId) {
        for (OnLeftChatListener listener : onLeftChatListeners) {
            listener.onLeftChatListener(conversationId, userId);
        }
    }

    public void addOnChatCreatedListener(OnChatCreatedListener listener) {
        onChatCreatedListeners.add(listener);
    }

    public void removeOnChatCreatedListener(OnChatCreatedListener listener) {
        onChatCreatedListeners.remove(listener);
    }

    protected void notifyOnChatCreatedListener(String conversationId, boolean createLocally) {
        for (OnChatCreatedListener listener : onChatCreatedListeners) {
            listener.onChatCreated(conversationId, createLocally);
        }
    }
}
