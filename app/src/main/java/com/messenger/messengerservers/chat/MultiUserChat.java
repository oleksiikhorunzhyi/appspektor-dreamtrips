package com.messenger.messengerservers.chat;


import com.messenger.messengerservers.MultiUserChatStateChanged;
import com.messenger.messengerservers.entities.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;


public abstract class MultiUserChat extends Chat {

    protected static final int MAX_GROUP_MEMBERS = 100;

    protected final ArrayList<MultiUserChatStateChanged> multiUserChatStateListeners = new ArrayList<>();

    public abstract void invite(List<User> users);

    public abstract Observable<List<User>> kick(List<User> users);

    public abstract void join(User user);

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

    protected void notifyParticipantsChanged(List<User> participants) {
        for (MultiUserChatStateChanged listener : multiUserChatStateListeners) {
            listener.onParticipantListChanged(participants);
        }
    }

    @Override
    public void close() {
        super.close();
        multiUserChatStateListeners.clear();
    }
}
