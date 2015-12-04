package com.messenger.messengerservers.xmpp.providers;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

public class ConversationProvider extends IQProvider<ConversationsPacket>{

    @Override
    public ConversationsPacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ConversationsPacket conversationsPacket = new ConversationsPacket();

        String elementName;
        Message message = null;
        Conversation conversation = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            String subject = parser.getAttributeValue("", "subject");
                            String type = parser.getAttributeValue("", "type");
                            String thread = parser.getAttributeValue("", "thread");
                            conversation = new Conversation(thread, subject, Conversation.Type.valueOf(type));
                            break;
                        case "last-message":
                            String from = parser.getAttributeValue("", "from");
                            String date = parser.getAttributeValue("", "time");
                            String messageBody = parser.nextText();
                            // TODO: id == null
                            message = new Message(JidCreatorHelper.obtainUser(from), null, messageBody, null);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            conversation.setLastMessage(message);
                            conversationsPacket.addConversation(conversation);
                            break;
                        case "list":
                            done = true;
                            break;
                    }
                    break ;
            }
        }
        return conversationsPacket;
    }
}
