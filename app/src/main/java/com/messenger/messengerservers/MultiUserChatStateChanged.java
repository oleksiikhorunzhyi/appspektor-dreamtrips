package com.messenger.messengerservers;

import com.messenger.messengerservers.entities.User;

import java.util.List;

public interface MultiUserChatStateChanged {

    void onSubjectChanged(String newSubject);

    void onParticipantListChanged(List<User> participants);

}
