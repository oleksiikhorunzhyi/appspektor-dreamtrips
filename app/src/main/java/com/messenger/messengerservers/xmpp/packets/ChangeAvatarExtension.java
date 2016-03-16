package com.messenger.messengerservers.xmpp.packets;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class ChangeAvatarExtension implements ExtensionElement {
    public static final String ELEMENT = "icon";

    private String avatarUrl;

    public ChangeAvatarExtension(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.rightAngleBracket();
        xml.append(avatarUrl);
        xml.closeElement(ELEMENT);
        return xml;
    }
}
