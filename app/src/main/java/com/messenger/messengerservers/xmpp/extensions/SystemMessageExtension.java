package com.messenger.messengerservers.xmpp.extensions;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;

import timber.log.Timber;

public class SystemMessageExtension implements ExtensionElement {
    public static final String NAMESPACE = "worldventures.com#user";
    public static final String ELEMENT = "service";

    private String from;
    private String to;
    private String type;
    private long timestamp;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML() {
        return new XmlStringBuilder(this);
    }

    public static final ExtensionElementProvider<ExtensionElement> PROVIDER
            = new ExtensionElementProvider<ExtensionElement>() {
        @Override
        public SystemMessageExtension parse(XmlPullParser parser, int initialDepth) {
            SystemMessageExtension systemMessage = new SystemMessageExtension();
            try {
                do {
                    if (parser.getEventType() == XmlPullParser.START_TAG && ELEMENT.equals(parser.getName())) {
                        String to = parser.getAttributeValue("", "to");
                        if (!TextUtils.isEmpty(to)) {
                            systemMessage.setTo(to);
                        }
                        systemMessage.setFrom(parser.getAttributeValue("", "from"));
                        systemMessage.setTimestamp(ParserUtils.getLongAttribute(parser, "timestamp", 0));
                        systemMessage.setType(parser.getAttributeValue("", "type"));
                    }
                    parser.next();
                } while (parser.getEventType() != XmlPullParser.END_TAG);
            } catch (Exception ex) {
                Timber.e(ex, "Could not parse system message extension");
            }
            return systemMessage;
        }
    };
}
