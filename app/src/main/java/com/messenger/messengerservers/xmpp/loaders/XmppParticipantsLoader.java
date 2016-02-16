package com.messenger.messengerservers.xmpp.loaders;

import android.support.annotation.NonNull;

import com.messenger.messengerservers.loaders.ParticipantsLoader;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.util.ParticipantProvider;

import java.util.List;

import rx.Observable;

public class XmppParticipantsLoader implements ParticipantsLoader {
    private final ParticipantProvider provider;
    private XmppServerFacade facade;

    public XmppParticipantsLoader(XmppServerFacade facade) {
        this.facade = facade;
        provider = new ParticipantProvider(facade.getConnection());
    }

    @Override
    public Observable<List<Participant>> load(@NonNull String conversationId) {
        if (conversationId.contains(facade.getUsername())) {
            return loadFromSingleChat(conversationId);
        } else {
            return loadFromGroupChat(conversationId);
        }
    }

    private Observable<List<Participant>> loadFromSingleChat(String conversationId) {
        return Observable.create(subscriber -> {
            List<Participant> participants = provider.getSingleChatParticipants(conversationId);
            if (subscriber.isUnsubscribed()) return;
            subscriber.onNext(participants);
            subscriber.onCompleted();
        });
    }

    private Observable<List<Participant>> loadFromGroupChat(String conversationId) {
        return Observable.create(subscriber -> {
            provider.loadMultiUserChatParticipants(conversationId, (owner, members, abandoned) -> {
                if (subscriber.isUnsubscribed()) return;
                members.add(owner);
                subscriber.onNext(members);
                subscriber.onCompleted();
            });
        });
    }
}
