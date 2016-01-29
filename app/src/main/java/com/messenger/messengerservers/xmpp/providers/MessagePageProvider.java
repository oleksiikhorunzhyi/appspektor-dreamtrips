package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.messengerservers.xmpp.packets.MessagePagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import timber.log.Timber;

public class MessagePageProvider extends IQProvider<MessagePagePacket> {

    private final Gson gson;

    public MessagePageProvider(Gson gson) {
        this.gson = gson;
    }

    @Override
    public MessagePagePacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        MessagePagePacket messagePagePacket = new MessagePagePacket();

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
                        case "to":
                            long timestamp = ParserUtils.getLongAttribute(parser, "secs");
                            String jid = parser.getAttributeValue("", "jid");
                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            Boolean unread = ParserUtils.getBooleanAttribute(parser, "unread");
                            messageBuilder = new Message.Builder()
                                    .id(messageId)
                                    .status((unread == null || !unread) ? MessageStatus.READ : MessageStatus.SENT)
                                    .date(timestamp)
                                    .fromId(JidCreatorHelper.obtainId(jid));
                            break;
                        case "body":
                            String messageBody = StringEscapeUtils.unescapeXml(parser.nextText());

                            MessageBody stanzaMessageBody = null;
                            try {
                                stanzaMessageBody = gson.fromJson(messageBody, MessageBody.class);
                            } catch (JsonSyntaxException e) {
                                Timber.w(e, getClass().getName());
                            }
                            //noinspection all //messageBuilder cannot be null
                            messageBuilder.messageBody(stanzaMessageBody);

                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "to":
                        case "from":
                            if (messageBuilder == null) continue;
                            Message message = messageBuilder.build();
                            if (TextUtils.isEmpty(message.getId())) continue;
                            messagePagePacket.add(message);
                            break;
                        case "chat":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return messagePagePacket;
    }
}
