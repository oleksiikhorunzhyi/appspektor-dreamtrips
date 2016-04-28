package com.messenger.messengerservers.xmpp.stanzas;

import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

public class ObtainConversationIQ extends DiscoverInfo {

    public static final String NAMESPACE = DiscoverInfo.NAMESPACE;
    public static final String ELEMENT = DiscoverInfo.QUERY_ELEMENT;

    public ObtainConversationIQ(String conversationId) {
        super();
        setType(IQ.Type.get);
        setTo(JidCreatorHelper.obtainGroupJid(conversationId));
    }
}

