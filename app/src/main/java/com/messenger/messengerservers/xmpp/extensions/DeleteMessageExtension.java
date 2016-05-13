package com.messenger.messengerservers.xmpp.extensions;

import android.text.TextUtils;

import com.messenger.messengerservers.model.DeletedMessage;
import com.messenger.messengerservers.model.ImmutableDeletedMessage;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteMessageExtension implements ExtensionElement {

    public static final String NAMESPACE = "worldventures.com#user";
    public static final String ELEMENT = "x";

    private List<DeletedMessage> deletedMessageList;

    public DeleteMessageExtension(List<DeletedMessage> deletedMessageList) {
        this.deletedMessageList = deletedMessageList;
    }

    public List<DeletedMessage> getDeletedMessageList() {
        return deletedMessageList;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML() {
        // not needed now
        XmlStringBuilder xml = new XmlStringBuilder(this);
        return xml;
    }

    public static class Provider extends ExtensionElementProvider<DeleteMessageExtension> {

        @Override
        public DeleteMessageExtension parse(XmlPullParser parser, int initialDepth) throws IOException, XmlPullParserException {
            List<DeletedMessage> deletedMessages = new ArrayList<>();

            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                String elementName = parser.getName();

                if (eventType == XmlPullParser.START_TAG && TextUtils.equals(elementName, "deleted-message")) {
                    String source = parser.getAttributeValue("", "by");
                    String messageId = parser.getAttributeValue("", "client_msg_id");
                    deletedMessages.add(ImmutableDeletedMessage.builder()
                            .source(source)
                            .messageId(messageId)
                            .build());
                } else if (eventType == XmlPullParser.END_TAG
                        && elementName.equalsIgnoreCase(ELEMENT)) {
                    done = true;
                }
            }

            return new DeleteMessageExtension(deletedMessages);
        }
    }

}
