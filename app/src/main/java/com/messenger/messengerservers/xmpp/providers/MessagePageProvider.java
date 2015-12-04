package com.messenger.messengerservers.xmpp.providers;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.packets.MessagePagePacket;

public class MessagePageProvider extends IQProvider<MessagePagePacket> {

    @Override
    public MessagePagePacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        MessagePagePacket messagePagePacket = new MessagePagePacket();
        String elementName;

        String jid = "";
        long date;
        String body = "";

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "from":
                        case "to":
                            long subject = ParserUtils.getLongAttribute(parser, "secs");
                            jid = parser.getAttributeValue("", "jid");
                            break;
                        case "body":
                            body = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "to":
                        case "from":
                            Message message = new Message(new User(jid), null, body, null);
                            messagePagePacket.add(message);
                            break;
                        case "chat":
                            done = true;
                            break;
                    }
                    break ;
            }
        }
        return messagePagePacket;
    }
}
