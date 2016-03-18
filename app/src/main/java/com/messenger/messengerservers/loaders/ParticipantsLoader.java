package com.messenger.messengerservers.loaders;

import com.messenger.messengerservers.model.Participant;

import java.util.List;

import rx.Observable;

public interface ParticipantsLoader {

    Observable<List<Participant>> load(String conversationId);
}
