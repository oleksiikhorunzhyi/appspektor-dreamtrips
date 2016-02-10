package com.messenger.messengerservers.chat;


import com.messenger.messengerservers.MultiUserChatStateChanged;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;


public abstract class MultiUserChat extends Chat {

    protected static final int MAX_GROUP_MEMBERS = 100;

    protected final List<MultiUserChatStateChanged> multiUserChatStateListeners = new CopyOnWriteArrayList<>();

    public abstract void invite(List<String> userIds);

    public abstract Observable<List<String>> kick(List<String> userIds);

    public abstract void join(String userId);

    public abstract void leave();

    public abstract Observable<MultiUserChat> setSubject(String subject);

    public void addMultiUserChatStateChangeListener(MultiUserChatStateChanged listener) {
        multiUserChatStateListeners.add(listener);
    }

    public void removeMultiUserChatStateChangeListener(MultiUserChatStateChanged listener) {
        multiUserChatStateListeners.remove(listener);
    }

    protected void notifySubjectUpdateListeners(String subject) {
        for (MultiUserChatStateChanged listener : multiUserChatStateListeners) {
            listener.onSubjectChanged(subject);
        }
    }

    protected void notifyParticipantsChanged(List<String> participantIds) {
        for (MultiUserChatStateChanged listener : multiUserChatStateListeners) {
            listener.onParticipantListChanged(participantIds);
        }
    }

    @Override
    public void close() {
        super.close();
        multiUserChatStateListeners.clear();
    }
}
