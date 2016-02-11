package com.messenger.messengerservers.xmpp.util;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import timber.log.Timber;

public class ParseUtils {

    public static Presence parsePresence(XmlPullParser parser)
            throws XmlPullParserException, IOException, SmackException {
        ParserUtils.assertAtStartTag(parser);
        final int initialDepth = parser.getDepth();

        Presence.Type type = Presence.Type.available;
        String typeString = parser.getAttributeValue("", "type");
        if (typeString != null && !typeString.equals("")) {
            if (typeString.equals("leave")) {
                type = Presence.Type.unsubscribed;
            } else {
                type = Presence.Type.fromString(typeString);
            }
        }
        Presence presence = new Presence(type);
        presence.setTo(parser.getAttributeValue("", "to"));
        presence.setFrom(parser.getAttributeValue("", "from"));
        presence.setStanzaId(parser.getAttributeValue("", "id"));

        // Parse sub-elements
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String elementName = parser.getName();
                    String namespace = parser.getNamespace();
                    switch(elementName) {
                        case "status":
                            presence.setStatus(parser.nextText());
                            break;
                        case "priority":
                            int priority = Integer.parseInt(parser.nextText());
                            presence.setPriority(priority);
                            break;
                        case "show":
                            String modeText = parser.nextText();
                            if (StringUtils.isNotEmpty(modeText)) {
                                presence.setMode(Presence.Mode.fromString(modeText));
                            } else {
                                // Some implementations send presence stanzas with a
                                // '<show />' element, which is a invalid XMPP presence
                                // stanza according to RFC 6121 4.7.2.1
                                Timber.w("Empty or null mode text in presence show element form "
                                        + presence.getFrom()
                                        + " with id '"
                                        + presence.getStanzaId()
                                        + "' which is invalid according to RFC6121 4.7.2.1");
                            }
                            break;
                        case "error":
                            presence.setError(PacketParserUtils.parseError(parser));
                            break;
                        default:
                            // Otherwise, it must be a packet extension.
                            // Be extra robust: Skip PacketExtensions that cause Exceptions, instead of
                            // failing completely here. See SMACK-390 for more information.
                            try {
                                PacketParserUtils.addExtensionElement(presence, parser, elementName, namespace);
                            } catch (Exception e) {
                                Timber.w(e, "Failed to parse extension packet in Presence packet.");
                            }
                            break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
            }
        }
        return presence;
    }
}