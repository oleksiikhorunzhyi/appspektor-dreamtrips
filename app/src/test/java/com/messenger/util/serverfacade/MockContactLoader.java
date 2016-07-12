package com.messenger.util.serverfacade;

import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.model.MessengerUser;

import java.util.List;

import rx.Observable;

public class MockContactLoader implements ContactsLoader {

    List<MessengerUser> messengerUsers;

    public MockContactLoader(List<MessengerUser> messengerUsers) {
        this.messengerUsers = messengerUsers;
    }

    @Override
    public Observable<List<MessengerUser>> load() {
        return Observable.just(messengerUsers);
    }
}
