package com.messenger.messengerservers.xmpp.util;

import android.text.TextUtils;

import com.messenger.messengerservers.constant.MessageType;
import com.messenger.messengerservers.xmpp.stanzas.incoming.LeftRoomPresence;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessageDeletedPresence;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ParseUtils {

    private static Stanza parseDeletedPresence(XmlPullParser parser)
            throws XmlPullParserException, IOException, SmackException {
        MessageDeletedPresence presence = new MessageDeletedPresence();
        parseStanza(parser, presence);
        return presence;
    }

    private static Stanza parseLeftPresence(XmlPullParser parser)
            throws XmlPullParserException, IOException, SmackException {
        LeftRoomPresence presence = new LeftRoomPresence();
        parseStanza(parser, presence);
        return presence;
    }

    public static Stanza parsePresence(XmlPullParser parser)
            throws XmlPullParserException, IOException, SmackException {
        ParserUtils.assertAtStartTag(parser);
        final int initialDepth = parser.getDepth();

        Presence.Type type = Presence.Type.available;

        String typeString = parser.getAttributeValue("", "type");
        if (!TextUtils.isEmpty(typeString)) {
            if (TextUtils.equals(typeString, MessageDeletedPresence.TYPE)) {
                return parseDeletedPresence(parser);
            } else if (TextUtils.equals(typeString, LeftRoomPresence.TYPE)) {
                return parseLeftPresence(parser);
            } else {
                type = Presence.Type.fromString(typeString);
            }
        }
        Presence presence = new Presence(type);
        presence.setTo(parser.getAttributeValue("", "to"));
        presence.setFrom(parser.getAttributeValue("", "from"));
        presence.setStanzaId(parser.getAttributeValue("", "id"));
        presence.setStatus(parser.getAttributeValue("", "status"));

        // Parse sub-elements
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String elementName = parser.getName();
                    String namespace = parser.getNamespace();
                    switch (elementName) {
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

    private static void parseStanza(XmlPullParser parser, Stanza stanza)
            throws XmlPullParserException, IOException, SmackException {
        final int initialDepth = parser.getDepth();

        stanza.setTo(parser.getAttributeValue("", "to"));
        stanza.setFrom(parser.getAttributeValue("", "from"));
        stanza.setStanzaId(parser.getAttributeValue("", "id"));

        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String elementName = parser.getName();
                    String namespace = parser.getNamespace();
                    switch (elementName) {
                        case "error":
                            stanza.setError(PacketParserUtils.parseError(parser));
                            break;
                        default:
                            // Otherwise, it must be a packet extension.
                            // Be extra robust: Skip PacketExtensions that cause Exceptions, instead of
                            // failing completely here. See SMACK-390 for more information.
                            try {
                                PacketParserUtils.addExtensionElement(stanza, parser, elementName, namespace);
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
    }

    /**
     * Parses a message packet.
     *
     * @param parser the XML parser, positioned at the start of a message packet.
     * @return a Message packet.
     * @throws IOException
     * @throws XmlPullParserException
     * @throws SmackException
     */
    public static Message parseMessage(XmlPullParser parser)
            throws XmlPullParserException, IOException, SmackException {
        ParserUtils.assertAtStartTag(parser);
        assert(parser.getName().equals(Message.ELEMENT));

        final int initialDepth = parser.getDepth();
        Message message = new Message();
        message.setStanzaId(parser.getAttributeValue("", "id"));
        message.setTo(parser.getAttributeValue("", "to"));
        message.setFrom(parser.getAttributeValue("", "from"));
        String typeString = parser.getAttributeValue("", "type");

        // add this so that service messages can be processed by Smack,
        // as they break standard XMPP contract on predefined set of message types
        if ("service".equals(typeString)) {
            message.setType(Message.Type.groupchat);
        } else {
            message.setType(Message.Type.fromString(typeString));
        }
        String language = getLanguageAttribute(parser);

        // determine message's default language
        String defaultLanguage = null;
        if (language != null && !"".equals(language.trim())) {
            message.setLanguage(language);
            defaultLanguage = language;
        }
        else {
            defaultLanguage = Stanza.getDefaultLanguage();
        }

        // Parse sub-elements. We include extra logic to make sure the values
        // are only read once. This is because it's possible for the names to appear
        // in arbitrary sub-elements.
        String thread = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String elementName = parser.getName();
                    String namespace = parser.getNamespace();
                    switch(elementName) {
                        case "subject":
                            String xmlLangSubject = getLanguageAttribute(parser);
                            if (xmlLangSubject == null) {
                                xmlLangSubject = defaultLanguage;
                            }

                            String subject = parseElementText(parser);

                            if (message.getSubject(xmlLangSubject) == null) {
                                message.addSubject(xmlLangSubject, subject);
                            }
                            break;
                        case Message.BODY:
                            String xmlLang = getLanguageAttribute(parser);
                            if (xmlLang == null) {
                                xmlLang = defaultLanguage;
                            }

                            String body = parseElementText(parser);

                            if (message.getBody(xmlLang) == null) {
                                message.addBody(xmlLang, body);
                            }
                            break;
                        case "thread":
                            if (thread == null) {
                                thread = parser.nextText();
                            }
                            break;
                        case "error":
                            message.setError(parseError(parser));
                            break;
                        default:
                            PacketParserUtils.addExtensionElement(message, parser, elementName, namespace);
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

        message.setThread(thread);
        return message;
    }

    /**
     * Returns the textual content of an element as String. After this method returns the parser
     * position will be END_TAG, following the established pull parser calling convention.
     * <p>
     * The parser must be positioned on a START_TAG of an element which MUST NOT contain Mixed
     * Content (as defined in XML 3.2.2), or else an XmlPullParserException will be thrown.
     * </p>
     * This method is used for the parts where the XMPP specification requires elements that contain
     * only text or are the empty element.
     *
     * @param parser
     * @return the textual content of the element as String
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static String parseElementText(XmlPullParser parser) throws XmlPullParserException, IOException {
        assert (parser.getEventType() == XmlPullParser.START_TAG);
        String res;
        if (parser.isEmptyElementTag()) {
            res = "";
        }
        else {
            // Advance to the text of the Element
            int event = parser.next();
            if (event != XmlPullParser.TEXT) {
                if (event == XmlPullParser.END_TAG) {
                    // Assume this is the end tag of the start tag at the
                    // beginning of this method. Typical examples where this
                    // happens are body elements containing the empty string,
                    // ie. <body></body>, which appears to be valid XMPP, or a
                    // least it's not explicitly forbidden by RFC 6121 5.2.3
                    return "";
                } else {
                    throw new XmlPullParserException(
                            "Non-empty element tag not followed by text, while Mixed Content (XML 3.2.2) is disallowed");
                }
            }
            res = parser.getText();
            event = parser.next();
            if (event != XmlPullParser.END_TAG) {
                throw new XmlPullParserException(
                        "Non-empty element tag contains child-elements, while Mixed Content (XML 3.2.2) is disallowed");
            }
        }
        return res;
    }

    private static String getLanguageAttribute(XmlPullParser parser) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attributeName = parser.getAttributeName(i);
            if ( "xml:lang".equals(attributeName) ||
                    ("lang".equals(attributeName) &&
                            "xml".equals(parser.getAttributePrefix(i)))) {
                return parser.getAttributeValue(i);
            }
        }
        return null;
    }

    /**
     * Parses error sub-packets.
     *
     * @param parser the XML parser.
     * @return an error sub-packet.
     * @throws IOException
     * @throws XmlPullParserException
     * @throws SmackException
     */
    public static XMPPError parseError(XmlPullParser parser)
            throws XmlPullParserException, IOException, SmackException {
        final int initialDepth = parser.getDepth();
        Map<String, String> descriptiveTexts = null;
        XMPPError.Condition condition = null;
        String conditionText = null;
        List<ExtensionElement> extensions = new ArrayList<ExtensionElement>();

        // Parse the error header
        XMPPError.Type errorType = XMPPError.Type.fromString(parser.getAttributeValue("", "type"));
        String errorGenerator = parser.getAttributeValue("", "by");

        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    String namespace = parser.getNamespace();
                    switch (namespace) {
                        case XMPPError.NAMESPACE:
                            switch (name) {
                                case Stanza.TEXT:
                                    descriptiveTexts = parseDescriptiveTexts(parser, descriptiveTexts);
                                    break;
                                default:
                                    condition = XMPPError.Condition.fromString(name);
                                    if (!parser.isEmptyElementTag()) {
                                        conditionText = parser.nextText();
                                    }
                                    break;
                            }
                            break;
                        default:
                            PacketParserUtils.addExtensionElement(extensions, parser, name, namespace);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
            }
        }
        return new XMPPError(condition, conditionText, errorGenerator, errorType, descriptiveTexts, extensions);
    }

    public static Map<String, String> parseDescriptiveTexts(XmlPullParser parser, Map<String, String> descriptiveTexts)
            throws XmlPullParserException, IOException {
        if (descriptiveTexts == null) {
            descriptiveTexts = new HashMap<String, String>();
        }
        String xmllang = getLanguageAttribute(parser);
        String text = parser.nextText();
        String previousValue = descriptiveTexts.put(xmllang, text);
        assert (previousValue == null);
        return descriptiveTexts;
    }

    public static @MessageType.Type String parseMessageType(String messageType) {
        switch (messageType) {
            case "join":
                return MessageType.SYSTEM_JOIN;
            case "leave":
                return MessageType.SYSTEM_LEAVE;
            case "kick":
                return MessageType.SYSTEM_KICK;
            default:
                return MessageType.MESSAGE;
        }
    }
}
