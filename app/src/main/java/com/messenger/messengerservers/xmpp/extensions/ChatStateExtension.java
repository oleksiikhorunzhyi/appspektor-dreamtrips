package com.messenger.messengerservers.xmpp.extensions;

import android.text.TextUtils;

import com.messenger.messengerservers.chat.ChatState;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;

import timber.log.Timber;

public class ChatStateExtension implements ExtensionElement {
    public static final String NAMESPACE = "jabber:x:event";
    public static final String ELEMENT = "x";
    @ChatState.State
    private final String state;

    /**
     * Default constructor. The argument provided is the state that the extension will represent.
     *
     * @param state the state that the extension represents.
     */
    public ChatStateExtension(@ChatState.State String state) {
        this.state = state;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @ChatState.State
    public String getChatState() {
        return state;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        if (TextUtils.equals(state, ChatState.COMPOSING)) {
            xml.rightAngleBracket();
            xml.emptyElement(state);
            xml.closeElement(ELEMENT);
        } else {
            xml.closeEmptyElement();
        }
        return xml;
    }

    public static class Provider extends ExtensionElementProvider<ChatStateExtension> {

        @Override
        public ChatStateExtension parse(XmlPullParser parser, int initialDepth) {
            String state = ChatState.PAUSE;
            try {
                while (!(XmlPullParser.END_TAG == parser.next() && TextUtils.equals(ELEMENT, parser.getName()))) {
                    String name = parser.getName();
                    if (name == null) continue;
                    switch (name) {
                        case "composing":
                            return new ChatStateExtension(ChatState.COMPOSING);
                    }
                }
            }
            catch (Exception ex) {
                Timber.e(ex, "ChatStateExtension pars xml");
            }
            return new ChatStateExtension(state);
        }
    }
}
