package com.messenger.messengerservers.chat;


import java.util.List;

import rx.Observable;


public interface MultiUserChat extends Chat {

    void invite(List<String> userIds);

    Observable<List<String>> kick(List<String> userIds);

    void join(String userId);

    void leave();

    Observable<MultiUserChat> setSubject(String subject);

    Observable<MultiUserChat> setAvatar(String avatar);
}
