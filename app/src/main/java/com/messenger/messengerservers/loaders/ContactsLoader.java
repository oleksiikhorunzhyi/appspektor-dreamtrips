package com.messenger.messengerservers.loaders;

import com.messenger.messengerservers.model.MessengerUser;

import java.util.List;

import rx.Observable;

public interface ContactsLoader {

   Observable<List<MessengerUser>> load();
}
