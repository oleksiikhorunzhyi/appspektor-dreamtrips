package com.messenger.messengerservers;

import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.listeners.OnAvatarChangedListener;
import com.messenger.messengerservers.listeners.OnChatCreatedListener;
import com.messenger.messengerservers.listeners.OnChatJoinedListener;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.messengerservers.listeners.OnFriendsChangedListener;
import com.messenger.messengerservers.listeners.OnSubjectChangedListener;
import com.messenger.messengerservers.listeners.OnUserStatusChangedListener;
import com.messenger.messengerservers.listeners.PresenceListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.UnhandledMessageListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import timber.log.Timber;

public abstract class GlobalEventEmitter {
    protected static final int EVENT_PRE_OUTGOING = 0x777;
    protected static final int EVENT_OUTGOING = 0x778;
    protected static final int EVENT_INCOMING = 0x779;
    protected List<GlobalMessageListener> globalMessageListeners = new CopyOnWriteArrayList<>();
    protected List<PresenceListener> presenceListeners = new CopyOnWriteArrayList<>();
    protected List<InvitationListener> invitationListeners = new CopyOnWriteArrayList<>();
    protected List<UnhandledMessageListener> unhandledMessageListeners = new CopyOnWriteArrayList<>();
    protected List<OnSubjectChangedListener> onSubjectChangedListeners = new CopyOnWriteArrayList<>();
    protected List<OnAvatarChangedListener> onAvatarChangedListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatLeftListener> onChatLeftListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatJoinedListener> onChatJoinedListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatCreatedListener> onChatCreatedListeners = new CopyOnWriteArrayList<>();
    protected List<OnChatStateChangedListener> onChatStateChangedListeners = new CopyOnWriteArrayList<>();
    protected List<OnUserStatusChangedListener> onUserStatusChangedListeners = new CopyOnWriteArrayList<>();
    protected List<OnFriendsChangedListener> onFriendsChangedListeners = new CopyOnWriteArrayList<>();

    public void addGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.add(listener);
    }

    public void removeGlobalMessageListener(GlobalMessageListener listener) {
        globalMessageListeners.remove(listener);
    }

    protected void notifyGlobalMessage(Message message, int eventType) {
        switch (eventType) {
            case EVENT_PRE_OUTGOING:
                for (GlobalMessageListener listener : globalMessageListeners) listener.onPreSendMessage(message);
                break;
            case EVENT_OUTGOING:
                for (GlobalMessageListener listener : globalMessageListeners) listener.onSendMessage(message);
                break;
            case EVENT_INCOMING:
                for (GlobalMessageListener listener : globalMessageListeners) listener.onReceiveMessage(message);
                break;
            default:
                break;
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

    protected void notifyReceiveInvite(String roomId) {
        for (InvitationListener listener : invitationListeners) {
            listener.receiveInvite(roomId);
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

    public void addOnAvatarChangeListener(OnAvatarChangedListener listener) {
        onAvatarChangedListeners.add(listener);
    }

    public void removeOnAvatarChangeListener(OnAvatarChangedListener listener) {
        onAvatarChangedListeners.remove(listener);
    }

    protected void notifyOnAvatarStateChangedListener(String conversationId, String avatar) {
        for (OnAvatarChangedListener listener : onAvatarChangedListeners) {
            listener.onAvatarChangedListener(conversationId, avatar);
        }
    }

    public void addOnChatLeftListener(OnChatLeftListener listener) {
        onChatLeftListeners.add(listener);
    }

    public void removeOnChatLeftListener(OnChatLeftListener listener) {
        onChatLeftListeners.remove(listener);
    }

    protected void notifyOnChatLeftListener(String conversationId, String userId, boolean leave) {
        for (OnChatLeftListener listener : onChatLeftListeners) {
            listener.onChatLeft(conversationId, userId, leave);
        }
    }

    public void addOnChatJoinedListener(OnChatJoinedListener listener) {
        onChatJoinedListeners.add(listener);
    }

    public void removeOnChatJoinedListener(OnChatJoinedListener listener) {
        onChatJoinedListeners.remove(listener);
    }

    protected void notifyOnChatJoinedListener(Participant participant, boolean isOnline) {
        for (OnChatJoinedListener listener : onChatJoinedListeners) {
            listener.onChatJoined(participant, isOnline);
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

    public void addOnChatStateChangedListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.add(listener);
    }

    public void removeOnChatStateChangedListener(OnChatStateChangedListener listener) {
        onChatStateChangedListeners.remove(listener);
    }

    protected void notifyOnChatStateChangedListener(String conversationId, String userId, @ChatState.State String state) {
        Timber.d("TEST_STATUSES conv %s, user %s, state %s", conversationId, userId, state);
        for (OnChatStateChangedListener listener : onChatStateChangedListeners) {
            listener.onChatStateChanged(conversationId, userId, state);
        }
    }

    public void addOnUserStatusChangedListener(OnUserStatusChangedListener listener) {
        onUserStatusChangedListeners.add(listener);
    }

    public void removeOnUserStatusChangedListener(OnUserStatusChangedListener listener) {
        onUserStatusChangedListeners.remove(listener);
    }

    protected void notifyOnUserStatusChangedListener(String userId, boolean online) {
        for (OnUserStatusChangedListener listener : onUserStatusChangedListeners) {
            listener.onUserStatusChanged(userId, online);
        }
    }


    public void addOnFriendsChangedListener(OnFriendsChangedListener listener) {
        onFriendsChangedListeners.add(listener);
    }

    public void removeOnFriendsChangedListener(OnFriendsChangedListener listener) {
        onFriendsChangedListeners.remove(listener);
    }

    protected void notifyOnFriendsChangedListener(List<String> userIds, boolean isFriend) {
        for (OnFriendsChangedListener listener : onFriendsChangedListeners) {
            listener.onFriendsChangedListener(userIds, isFriend);
        }
    }

//    observables
    public Observable<JoinedEvent> createChatJoinedObservable() {
        OnChatJoinedListener[] onChatJoinedListeners = new OnChatJoinedListener[]{null};
        return Observable.<JoinedEvent>create(subscriber -> {
            OnChatJoinedListener listener = (participant, isOnline) -> {
                subscriber.onNext(new JoinedEvent(participant, isOnline));
            };
            addOnChatJoinedListener(listener);
            onChatJoinedListeners[0] = listener;
        })
                .doOnUnsubscribe(() -> removeOnChatJoinedListener(onChatJoinedListeners[0]));
    }
}