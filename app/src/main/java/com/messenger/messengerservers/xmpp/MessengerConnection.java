package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.xmpp.util.ParseUtils;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.parsing.UnparsablePacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;

public class MessengerConnection extends XMPPTCPConnection {

    public MessengerConnection(XMPPTCPConnectionConfiguration config) {
        super(config);
    }

    @Override
    protected void parseAndProcessStanza(XmlPullParser parser) throws Exception {
        ParserUtils.assertAtStartTag(parser);
        int parserDepth = parser.getDepth();
        Stanza stanza = null;
        try {
            if (Presence.ELEMENT.equals(parser.getName())) {
                stanza = ParseUtils.parsePresence(parser);
            } else {
                stanza = PacketParserUtils.parseStanza(parser);
            }
        }
        catch (Exception e) {
            CharSequence content = PacketParserUtils.parseContentDepth(parser,
                    parserDepth);
            UnparsablePacket message = new UnparsablePacket(content, e);
            ParsingExceptionCallback callback = getParsingExceptionCallback();
            if (callback != null) {
                callback.handleUnparsablePacket(message);
            }
        }
        ParserUtils.assertAtEndTag(parser);
        if (stanza != null) {
            processPacket(stanza);
        }
    }
}
