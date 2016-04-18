package com.messenger.messengerservers.xmpp.paginations;

import com.google.gson.Gson;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.MessagePageIQ;
import com.messenger.messengerservers.xmpp.stanzas.ObtainMessageListIQ;
import com.messenger.messengerservers.xmpp.providers.MessagePageProvider;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import timber.log.Timber;

public class XmppConversationHistoryPaginator extends PagePagination<Message> {
    private AbstractXMPPConnection connection;
    private String conversationId;
    private Gson gson;

    public XmppConversationHistoryPaginator(XmppServerFacade facade, String conversationId,
                                            int pageSize) {
        super(pageSize);
        this.gson = facade.getGson();
        this.connection = facade.getConnection();
        this.conversationId = conversationId;
    }

    @Override
    public void loadPage(int page, long sinceSecs) {
        ObtainMessageListIQ packet = new ObtainMessageListIQ();
        packet.setMax(getSizePerPage());
        packet.setConversationId(conversationId);
        packet.setPage(page);
        packet.setSinceSec(sinceSecs);
        Timber.i("Send XMPP Packet: %s", packet.toString());

        try {
            ProviderManager.addIQProvider(MessagePageIQ.ELEMENT_CHAT, MessagePageIQ.NAMESPACE, new MessagePageProvider(gson));
            connection.sendStanzaWithResponseCallback(packet,
                    stanza -> stanza instanceof MessagePageIQ
                            || (stanza.getStanzaId() != null && stanza.getStanzaId().startsWith("page")),
                    stanzaPacket -> {
                        if (!(stanzaPacket instanceof MessagePageIQ)){
                            notifyError(new SmackException("No history"));
                        } else {
                            notifyLoaded(((MessagePageIQ) stanzaPacket).getMessages());
                        }
                        ProviderManager.removeIQProvider(MessagePageIQ.ELEMENT_CHAT, MessagePageIQ.NAMESPACE);
                    },
                    exception -> {
                        Timber.e(exception, getClass().getName());
                        notifyError(exception);
                    });
        } catch (SmackException.NotConnectedException e) {
            Timber.i(e, "%s, Loading error", getClass().getSimpleName());
            notifyError(e);
        }
    }

    private void notifyLoaded(List<Message> messages){
        for (Message message : messages) {
            message.setConversationId(conversationId);
        }

        if (persister != null) {
            persister.save(messages);
        }

        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onLoaded(messages);
        }
    }

    private void notifyError(Exception e) {
        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onError(e);
        }
    }

    @Override
    public void close() {
        onEntityLoadedListener = null;
        persister = null;
    }
}
