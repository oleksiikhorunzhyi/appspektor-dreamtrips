package com.messenger.messengerservers.chat;


import java.util.List;

import rx.Observable;


public abstract class MultiUserChat extends Chat {

    public abstract void invite(List<String> userIds);

    public abstract Observable<List<String>> kick(List<String> userIds);

    public abstract void join(String userId);

    public abstract void leave();

    public abstract Observable<MultiUserChat> setSubject(String subject);
}
