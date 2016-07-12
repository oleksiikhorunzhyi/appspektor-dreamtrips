package com.messenger.messengerservers.chat;

import java.util.List;

import rx.Observable;

public interface GroupChat extends Chat {

    void invite(List<String> userIds);

    Observable<GroupChat> kick(String userId);

    void join(String userId);

    Observable<GroupChat> leave();

    Observable<GroupChat> setSubject(String subject);

    Observable<GroupChat> setAvatar(String avatar);
}
