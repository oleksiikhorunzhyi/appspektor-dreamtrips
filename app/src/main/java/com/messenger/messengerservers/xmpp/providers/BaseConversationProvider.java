package com.messenger.messengerservers.xmpp.providers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.messenger.delegate.MessageBodyParser;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.extensions.ChangeAvatarExtension;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public abstract class BaseConversationProvider<T extends IQ> extends IQProvider<T> {
    private static final String CONVERSATION = "chat";
    private static final String CONVERSATION_THREAD = "thread";
    private static final String CONVERSATION_UNREAD_COUNT = "unread-count";
//    private static final String CONVERSATION_LEFT_TIME = "left-room-date";
//    private static final String CONVERSATION_KICKED = "kicked";
    private static final String CONVERSATION_TYPE = "type";
    private static final String CONVERSATION_SUBJECT = "subject";

    private static final String LAST_MESSAGE = "last-message";
    private static final String LAST_MESSAGE_ID = "client_msg_id";
    private static final String LAST_MESSAGE_TIME = "time";
    private static final String LAST_MESSAGE_IS_UNREAD = "unread";
    private static final String LAST_MESSAGE_FROM = "from";

    private final MessageBodyParser messageBodyParser;

    public BaseConversationProvider(Gson gson) {
        this.messageBodyParser = new MessageBodyParser(gson);
    }

    @Override
    public T parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        List<Conversation> conversations = new ArrayList<>();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            String elementName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    switch (elementName) {
                        case CONVERSATION:
                            Conversation conversation = parseConversation(parser);
                            Timber.d("TEST_PARSER %s", conversation);
                            conversations.add(conversation);
                            break;
                    }
                    break;
            }
            done = eventType == XmlPullParser.END_TAG && getEndElement().equalsIgnoreCase(elementName);
        }
        return constructIQ(conversations);
    }

    @NonNull
    private Conversation parseConversation(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        String thread = parser.getAttributeValue("", CONVERSATION_THREAD);
        String type = getType(thread, parser);
        String subject = parser.getAttributeValue("", CONVERSATION_SUBJECT);
        String avatar = parser.getAttributeValue("", ChangeAvatarExtension.ELEMENT);
        int unreadMessageCount = ParserUtils.getIntegerAttribute(parser, CONVERSATION_UNREAD_COUNT, 0);

        Message lastMessage = null;
        long timestamp = 0;
        while (parser.next() != XmlPullParser.END_TAG && TextUtils.equals(LAST_MESSAGE, parser.getName())) {
            timestamp = ParserUtils.getLongAttribute(parser, LAST_MESSAGE_TIME, 0);
            lastMessage = parseLastMessage(parser, thread, timestamp);
            Timber.d("TEST_PARSER_MSG %s", lastMessage);
        }

        return new Conversation.Builder()
                .id(thread)
                .type(type.toLowerCase())
                .subject(subject)
                .avatar(avatar)
                .lastActiveDate(timestamp)
                .lastMessage(lastMessage)
                //// TODO: 1/19/16 set status depends on status will be sent in future
                .status(ConversationStatus.PRESENT)
                .unreadMessageCount(unreadMessageCount)
                .build();
    }

    @Nullable
    private Message parseLastMessage(XmlPullParser parser, String thread, long timestamp)
            throws IOException, XmlPullParserException {
        String messageId = parser.getAttributeValue("", LAST_MESSAGE_ID);
        if (TextUtils.isEmpty(messageId)) {
            return null;
        }

        boolean unread = ParserUtils.getBooleanAttribute(parser, LAST_MESSAGE_IS_UNREAD, false);
        String from = parser.getAttributeValue("", LAST_MESSAGE_FROM);
        // TODO: 4/28/16 Workaround for server bug:
        // domain name is missing from JID when getting single conversation details
        if (from.contains("@")) {
            from = JidCreatorHelper.obtainId(from);
        }

        return new Message.Builder()
                .id(messageId)
                .date(timestamp)
                .fromId(from)
                .conversationId(thread)
                .status(unread ? MessageStatus.SENT  : MessageStatus.READ)
                .messageBody(messageBodyParser.parseMessageBody(parser.nextText()))
                .build();
    }


    private String getType(String thread, XmlPullParser parser) {
        if (thread.startsWith("dreamtrip_auto_gen")) {
            return ConversationType.TRIP;
        }
        return parser.getAttributeValue("", CONVERSATION_TYPE);
    }

    protected abstract T constructIQ(List<Conversation> data);

    protected abstract String getEndElement();
}
