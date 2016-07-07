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

public class MessagePageProvider extends IQProvider<MessagePageIQ> {

    private MessageBodyParser messageBodyParser;

    public MessagePageProvider(Gson gson) {
        this.messageBodyParser = new MessageBodyParser(gson);
    }

    @Override
    public MessagePageIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        MessagePageIQ messagePageIQ = new MessagePageIQ();

        String elementName;
        Message.Builder messageBuilder = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "from":
                        case "to": {
                            long timestamp = ParserUtils.getLongAttribute(parser, "secs");
                            String fromId = JidCreatorHelper.obtainId(parser.getAttributeValue("", "jid"));
                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            Boolean unread = ParserUtils.getBooleanAttribute(parser, "unread");
                            String deleted = parser.getAttributeValue("", "deleted");

                            messageBuilder = new Message.Builder()
                                    .id(messageId)
                                    .deleted(deleted)
                                    .status((unread == null || !unread) ? MessageStatus.READ : MessageStatus.SENT)
                                    .date(timestamp)
                                    .fromId(fromId)
                                    .type(MessageType.MESSAGE);
                            break;
                        }
                        case "service": {
                            long timestamp = ParserUtils.getLongAttribute(parser, "timestamp");
                            String messageId = parser.getAttributeValue("", "id");
                            String fromIdAttr = parser.getAttributeValue("", "from");
                            String fromId = TextUtils.isEmpty(fromIdAttr) ? null : JidCreatorHelper.obtainId(fromIdAttr);

                            String type = parser.getAttributeValue("", "type");

                            messageBuilder = new Message.Builder()
                                    .id(messageId)
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
                        case "body":
                            //noinspection all //messageBuilder cannot be null
                            messageBuilder.messageBody(messageBodyParser.
                                    parseMessageBody(parser.nextText()));

                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "to":
                        case "from":
                        case "service":
                            if (messageBuilder == null) continue;
                            Message message = messageBuilder.build();
                            if (TextUtils.isEmpty(message.getId())) continue;
                            if (MessageType.MESSAGE.equals(message.getType()) && message.getMessageBody() == null) continue;
                            messagePageIQ.add(message);
                            break;
                        case "chat":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return messagePageIQ;
    }
}
