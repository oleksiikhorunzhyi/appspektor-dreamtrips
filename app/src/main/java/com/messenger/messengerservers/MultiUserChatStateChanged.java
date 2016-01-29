package com.messenger.messengerservers;


import java.util.List;

public interface MultiUserChatStateChanged {

    void onSubjectChanged(String newSubject);

    void onParticipantListChanged(List<String> participantsId);

}
