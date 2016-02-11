package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import timber.log.Timber;

public class ConversationProvider extends IQProvider<ConversationsPacket> {
    private final Gson gson;

    public ConversationProvider(Gson gson) {
        this.gson = gson;
    }

    @Override
    public ConversationsPacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ConversationsPacket conversationsPacket = new ConversationsPacket();

        String thread = null;
        String elementName;
        Message.Builder messageBuilder = null;
        Conversation.Builder conversationBuilder = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            thread = parser.getAttributeValue("", "thread");
                            String type = getTypeByThread(thread);
                            if (type == null) {
                                type = parser.getAttributeValue("", "type");
                            }
                            String subject = parser.getAttributeValue("", "subject");
                            int unreadMessegeCount = ParserUtils.getIntegerAttribute(parser, "unread-count");

                            conversationBuilder = new Conversation.Builder()
                                    .id(thread)
                                    .type(type.toLowerCase())
                                    .subject(subject)
                                    //// TODO: 1/19/16 set status depends on status will be sent in future
                                    .status(ConversationStatus.PRESENT)
                                    .unreadMessageCount(unreadMessegeCount);
                            break;
                        case "last-message":
                            long timestamp = ParserUtils.getLongAttribute(parser, "time");
                            //noinspection all
                            conversationBuilder.lastActiveDate(timestamp);

                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            if (TextUtils.isEmpty(messageId)) {
                                messageBuilder = null;
                                continue;
                            }
                            String from = parser.getAttributeValue("", "from");
                            String messageBody = StringEscapeUtils.unescapeXml(parser.nextText());
                            messageBuilder = new Message.Builder()
                                    .id(messageId)
                                    .date(timestamp)
                                    .fromId(JidCreatorHelper.obtainId(from));

                            MessageBody stanzaMessageBody = null;
                            try {
                                stanzaMessageBody = gson.fromJson(messageBody, MessageBody.class);
                            } catch (JsonSyntaxException e) {
                                Timber.w(e, getClass().getName());
                            }

                            messageBuilder.messageBody(stanzaMessageBody);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            if (conversationBuilder == null) break;
                            Conversation conversation = conversationBuilder
                                    .lastMessage(messageBuilder != null ? messageBuilder.conversationId(thread).build() : null)
                                    .build();
                            //noinspection all // conversationBuilder cannot be null
                            conversationsPacket.addConversation(conversation);
                            messageBuilder = null;
                            thread =  null;
                            conversationBuilder = null;
                            break;
                        case "list":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return conversationsPacket;
    }

    private String getTypeByThread(String thread) {
        if (thread.startsWith("dreamtrip_auto_gen")) {
            return ConversationType.TRIP;
        }
        return null;
    }
}