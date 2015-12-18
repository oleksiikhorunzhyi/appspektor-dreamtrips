package com.messenger.messengerservers.xmpp.paginations;

import android.util.Log;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.packets.MessagePagePacket;
import com.messenger.messengerservers.xmpp.packets.ObtainMessageListPacket;
import com.messenger.messengerservers.xmpp.providers.MessagePageProvider;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;


public class XmppConversationHistoryPaginator extends PagePagination<Message> {
    private AbstractXMPPConnection connection;
    private String conversationId;

    public XmppConversationHistoryPaginator(AbstractXMPPConnection connection, String conversationId,
                                            int pageSize) {
        super(pageSize);
        this.connection = connection;
        this.conversationId = conversationId;
    }

    @Override
    public void loadPage(int page, int sinceSecs) {
        ObtainMessageListPacket packet = new ObtainMessageListPacket();
        packet.setMax(getSizePerPage());
        packet.setConversationId(conversationId);
        packet.setPage(page);
        packet.setSinceSec(sinceSecs);
        Log.i("Send XMPP Packet: ", packet.toString());

        try {
            ProviderManager.addIQProvider(MessagePagePacket.ELEMENT_CHAT, MessagePagePacket.NAMESPACE, new MessagePageProvider());
            connection.sendStanzaWithResponseCallback(packet,
                    stanza -> stanza instanceof MessagePagePacket,
                    stanzaPacket -> {
                        notifyLoaded(((MessagePagePacket) stanzaPacket).getMessages());
                        ProviderManager.removeIQProvider(MessagePagePacket.ELEMENT_CHAT, MessagePagePacket.NAMESPACE);
                    },
                    exception -> {
                        Log.e("Error!!!", Log.getStackTraceString(exception));
                        notifyError(exception);
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.i("XmppMessagePagePaginate", "Loading error", e);
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

    private void notifyError(Exception e){
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
