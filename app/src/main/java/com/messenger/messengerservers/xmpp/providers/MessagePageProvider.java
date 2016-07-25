package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.messenger.delegate.MessageBodyParser;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessagePageIQ;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ParseUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MessagePageProvider extends IQProvider<MessagePageIQ> {

    private static final String ELEMENT_CHAT = "chat";
    private static final String ELEMENT_TO = "to";
    private static final String ELEMENT_FROM = "from";
    private static final String ELEMENT_BODY = "body";
    private static final String ELEMENT_SERVICE = "service";

    private MessageBodyParser messageBodyParser;

    public MessagePageProvider(Gson gson) {
        this.messageBodyParser = new MessageBodyParser(gson);
    }

    @Override
    public MessagePageIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ArrayList<Message> loadedMessages = new ArrayList<>();
        int loadedMessageCount = 0;
        String thread = null;
        Message.Builder messageBuilder = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.getEventType();
            String elementName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    switch (elementName) {
                        case ELEMENT_CHAT:
                            thread = parser.getAttributeValue("", "thread");
                            break;
                        case ELEMENT_FROM:
                        case ELEMENT_TO: {
                            loadedMessageCount++;
                            long timestamp = ParserUtils.getLongAttribute(parser, "secs");
                            String fromId = JidCreatorHelper.obtainId(parser.getAttributeValue("", "jid"));
                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            Boolean unread = ParserUtils.getBooleanAttribute(parser, "unread");
                            String deleted = parser.getAttributeValue("", "deleted");

                            messageBuilder = new Message.Builder()
                                    .id(messageId)
                                    .conversationId(thread)
                                    .deleted(deleted)
                                    .status((unread == null || !unread) ? MessageStatus.READ : MessageStatus.SENT)
                                    .date(timestamp)
                                    .fromId(fromId)
                                    .type(MessageType.MESSAGE);
                            break;
                        }
                        case ELEMENT_SERVICE: {
                            loadedMessageCount++;
                            long timestamp = ParserUtils.getLongAttribute(parser, "timestamp");
                            String messageId = parser.getAttributeValue("", "id");
                            String fromIdAttr = parser.getAttributeValue("", "from");
                            String fromId = TextUtils.isEmpty(fromIdAttr) ? null : JidCreatorHelper.obtainId(fromIdAttr);

                            String type = parser.getAttributeValue("", "type");

                            messageBuilder = new Message.Builder()
                                    .id(messageId)
                                    .conversationId(thread)
                                    .status(MessageStatus.READ)
                                    .date(timestamp)
                                    .type(ParseUtils.parseMessageType(type))
                                    .fromId(fromId);

                            String toJid = parser.getAttributeValue("", "to");
                            if (!TextUtils.isEmpty(toJid)) {
                                messageBuilder.toId(JidCreatorHelper.obtainId(toJid));
                            }

                            break;
                        }
                        case ELEMENT_BODY:
                            //noinspection all //messageBuilder cannot be null
                            messageBuilder.messageBody(messageBodyParser.
                                    parseMessageBody(parser.nextText()));

                    }
                    break;
                case XmlPullParser.END_TAG:
                    switch (elementName) {
                        case ELEMENT_TO:
                        case ELEMENT_FROM:
                        case ELEMENT_SERVICE:
                            if (messageBuilder == null) break;
                            Message message = messageBuilder.build();
                            if (TextUtils.isEmpty(message.getId())) break;
                            if (MessageType.MESSAGE.equals(message.getType()) && message.getMessageBody() == null) break;
                            loadedMessages.add(message);
                            break;
                        case ELEMENT_CHAT:
                            done = true;
                            continue;
                    }
                    break;
            }
            parser.next();
        }
        return new MessagePageIQ(loadedMessages, loadedMessageCount);
    }
}
