package com.messenger.messengerservers.xmpp.paginations;

import com.google.gson.Gson;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessagePageIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainMessageListIQ;
import com.messenger.messengerservers.xmpp.providers.MessagePageProvider;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class XmppConversationHistoryPaginator extends PagePagination<Message> {
    private final Observable<XMPPConnection> connectionObservable;
    private final Gson gson;

    public XmppConversationHistoryPaginator(XmppServerFacade facade, int pageSize) {
        super(pageSize);
        this.gson = facade.getGson();
        this.connectionObservable = facade.getConnectionObservable();
    }

    @Override
    public void loadPage(String conversationId, int page, long sinceSecs) {
        ObtainMessageListIQ packet = new ObtainMessageListIQ();
        packet.setMax(getPageSize());
        packet.setConversationId(conversationId);
        packet.setPage(page);
        packet.setSinceSec(sinceSecs);
        Timber.i("Send XMPP Packet: %s", packet.toString());

        ProviderManager.addIQProvider(MessagePageIQ.ELEMENT_CHAT, MessagePageIQ.NAMESPACE, new MessagePageProvider(gson));
        connectionObservable
                .take(1)
                .doOnNext(connection -> connectionPrepared(connection, packet, conversationId))
                .subscribe(connection -> {
                }, this::notifyError);
    }

    public void connectionPrepared(XMPPConnection connection, Stanza packet, String conversationId) {
        try {
            connection.sendStanzaWithResponseCallback(packet,
                    this::stanzaFilter, stanza -> stanzaCallback(stanza, conversationId), this::notifyError);
        } catch (Throwable e) {
            notifyError(e);
        }
    }

    private boolean stanzaFilter(Stanza stanza) {
        return stanza instanceof MessagePageIQ
                || (stanza.getStanzaId() != null && stanza.getStanzaId().startsWith("page"));
    }

    private void stanzaCallback(Stanza stanza, String conversationId) {
        if (!(stanza instanceof MessagePageIQ)) {
            notifyError(new IllegalArgumentException());
        } else {
            notifyLoaded(((MessagePageIQ) stanza).getMessages(), conversationId);
        }
        ProviderManager.removeIQProvider(MessagePageIQ.ELEMENT_CHAT, MessagePageIQ.NAMESPACE);
    }

    private void notifyLoaded(List<Message> messages, String conversationId) {
        for (Message message : messages) {
            message.setConversationId(conversationId);
        }

        publishSubject.onNext(messages);
    }

    private void notifyError(Throwable throwable) {
        publishSubject.onError(throwable);
    }
}
