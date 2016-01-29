package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.entities.ConversationWithLastMessage;
import com.messenger.messengerservers.xmpp.entities.MessageBody;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class ConversationProvider extends IQProvider<ConversationsPacket> {

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
                            String thread = parser.getAttributeValue("", "thread");
                            String type = getTypeByThread(thread);
                            if (type == null) {
                                type = parser.getAttributeValue("", "type");
                            }
                            String subject = parser.getAttributeValue("", "subject");
                            int unreadMessegeCount = ParserUtils.getIntegerAttribute(parser, "unread-count");
                            //noinspection all
                            conversation = new Conversation.Builder()
                                    .id(thread)
                                    .type(type.toLowerCase())
                                    .subject(subject)
                                    //// TODO: 1/19/16 set status depends on status will be sent in future
                                    .status(Conversation.Status.PRESENT)
                                    .unreadMessageCount(unreadMessegeCount)
                                    .build();
                            break;
                        case "last-message":
                            long timestamp = ParserUtils.getLongAttribute(parser, "time");
                            //noinspection all
                            conversation.setLastActiveDate(timestamp);

                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            if (TextUtils.isEmpty(messageId)) {
                                message = null;
                                continue;
                            }
                            String from = parser.getAttributeValue("", "from");
                            String messageBody = StringEscapeUtils.unescapeXml(parser.nextText());
                            Message.Builder builder = new Message.Builder()
                                    .id(messageId)
                                    .conversationId(conversation.getId())
                                    //// TODO: 12/18/15 today attribute secs is millisecond
                                    .date(new Date(timestamp))
                                    .from(JidCreatorHelper.obtainId(from));

                            MessageBody stanzaMessageBody = null;
                            try {
                                stanzaMessageBody = new Gson().fromJson(messageBody, MessageBody.class);
                            } catch (JsonSyntaxException e) {
                                Timber.e(e, "Parce error");
                            }

                            if (stanzaMessageBody == null || stanzaMessageBody.getLocale() == null || stanzaMessageBody.getText() == null) {
                                builder.text("")
                                        .locale(Locale.getDefault());
                            } else {
                                builder.text(stanzaMessageBody.getText())
                                        .locale(new Locale(stanzaMessageBody.getLocale()));
                            }

                            message = builder.build();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            conversationsPacket.addConversation(new ConversationWithLastMessage(conversation, message));
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

    @Conversation.Type.ConversationType
    private String getTypeByThread(String thread) {
        if (thread.startsWith("dreamtrip_auto_gen")) {
            return Conversation.Type.TRIP;
        }
        return null;
    }
}
