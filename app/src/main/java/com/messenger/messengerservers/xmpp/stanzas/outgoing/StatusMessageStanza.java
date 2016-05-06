package com.messenger.messengerservers.xmpp.stanzas.outgoing;


import com.messenger.messengerservers.xmpp.chats.Status;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class StatusMessageStanza extends Stanza {

    public static final String ELEMENT = "message";

    public final String messageId;
    public final String status;
    public final org.jivesoftware.smack.packet.Message.Type type;

    public StatusMessageStanza(String messageId, @Status.MessageStatus String status, String to,
                               org.jivesoftware.smack.packet.Message.Type type) {
        super();
        this.messageId = messageId;
        this.status = status;
        this.type = type;
        setTo(to);
    }

    @Override
    public CharSequence toXML() {
        XmlStringBuilder buf = new XmlStringBuilder();
        buf.halfOpenElement(ELEMENT);
        addCommonAttributes(buf);
        buf.optAttribute("type", type);
        buf.rightAngleBracket();

        buf.halfOpenElement("x");
        buf.attribute("xmlns", "jabber:x:event");
        buf.rightAngleBracket();

        buf.halfOpenElement("id");
        buf.rightAngleBracket();
        buf.append(messageId);
        buf.closeElement("id");
        buf.halfOpenElement(status);
        buf.closeEmptyElement();

        buf.append("</x>");

        buf.closeElement(ELEMENT);
        return buf;
    }

}
