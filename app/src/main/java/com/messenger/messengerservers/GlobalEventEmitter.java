package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.listeners.OnChatCreatedListener;
import com.messenger.messengerservers.listeners.OnChatJoinedListener;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.messengerservers.listeners.OnSubjectChangedListener;
import com.messenger.messengerservers.listeners.PresenceListener;
import com.messenger.messengerservers.xmpp.UnhandledMessageListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GlobalEventEmitter {
    protected List<GlobalMessageListener> globalMessageListeners = new CopyOnWriteArrayList<>();
    protected List<PresenceListener> presenceListeners = new CopyOnWriteArrayList<>();
    protected List<InvitationListener> invitationListeners = new CopyOnWriteArrayList<>();
    protected List<UnhandledMessageListener> unhandledMessageListeners = new CopyOnWriteArrayList<>();
    protected List<OnSubjectChangedListener> onSubjectChangedListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatLeftListener> onChatLeftListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatJoinedListener> onChatJoinedListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatCreatedListener> onChatCreatedListeners = new CopyOnWriteArrayList<>();

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

    public void addOnChatLeftListener(OnChatLeftListener listener) {
        onChatLeftListeners.add(listener);
    }

    public void removeOnChatLeftListener(OnChatLeftListener listener) {
        onChatLeftListeners.remove(listener);
    }

    protected void notifyOnChatLeftListener(String conversationId, String userId) {
        for (OnChatLeftListener listener : onChatLeftListeners) {
            listener.onChatLeft(conversationId, userId);
        }
    }

    public void addOnChatJoinedListener(OnChatJoinedListener listener) {
        onChatJoinedListeners.add(listener);
    }

    public void removeOnChatJoinedListener(OnChatJoinedListener listener) {
        onChatJoinedListeners.remove(listener);
    }

    protected void notifyOnChatJoinedListener(String conversationId, String userId, boolean isOnline) {
        for (OnChatJoinedListener listener : onChatJoinedListeners) {
            listener.onChatJoined(conversationId, userId, isOnline);
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