package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainConversationIQ;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;

import rx.Observable;

public class XmppConversationLoader extends XmppBaseConversationsLoader implements com.messenger.messengerservers.loaders.ConversationLoader {
    private final String conversationId;

    public XmppConversationLoader(XmppServerFacade facade, String conversationId) {
        super(facade);
        this.conversationId = conversationId;
        ProviderManager.addIQProvider(
                ConversationIQ.ELEMENT, ConversationIQ.NAMESPACE,
                new ConversationProvider(facade.getGson())
        );
    }

    @Override
    public Observable<Conversation> load() {
        return facade.getConnectionObservable()
                .flatMap(this::loadConversation)
                .flatMap(this::loadParticipants);
    }

    private Observable<Conversation> loadConversation(XMPPConnection connection) {
        return Observable.create(subscriber -> {
            try {
                connection.sendStanzaWithResponseCallback(createStanza(),
                        this::stanzaFilter, stanza -> {
                            subscriber.onNext(((ConversationIQ) stanza).getConversation());
                            subscriber.onCompleted();
                        }, subscriber::onError);
            } catch (SmackException.NotConnectedException e) {
                subscriber.onError(new ConnectionException(e));
            }
        });
    }

    protected Observable<Conversation> loadParticipants(Conversation conversation) {
        return obtainParticipants(new XmppParticipantsLoader(facade), conversation);
    }

    private boolean stanzaFilter(Stanza stanza) {
        return stanza instanceof ConversationIQ;
    }

    private Stanza createStanza() {
        return new ObtainConversationIQ(conversationId);
    }
}
