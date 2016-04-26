package com.messenger.messengerservers.xmpp.stanzas;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Example:
 *      <presence to='room-id@conference.worldventures.com/wvuser1' type='leave'>
 *          <priority>8</priority>
 *          <x xmlns="http://jabber.org/protocol/muc"/>
 *      </presence>
 */
public class LeavePresence extends Stanza {
    public static final String ELEMENT = "presence";
    public static final String TYPE_LEAVE = "leave";

    private int priority = 8;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public CharSequence toXML() {
        XmlStringBuilder buf = new XmlStringBuilder();
        buf.halfOpenElement(ELEMENT);
        addCommonAttributes(buf);

        buf.optAttribute("type", TYPE_LEAVE);
        buf.rightAngleBracket();

        buf.element("priority", Integer.toString(priority));

        buf.halfOpenElement("x");
        buf.attribute("xmlns", "http://jabber.org/protocol/muc");
        buf.closeEmptyElement();

        buf.closeElement(ELEMENT);
        return buf;
    }
}
