package com.messenger.messengerservers.xmpp.providers;


import android.text.TextUtils;

import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.packets.ConversationParticipants;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ConversationParticipantsProvider extends IQProvider<ConversationParticipants> {

    @Override
    public ConversationParticipants parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ConversationParticipants conversationParticipants = new ConversationParticipants();

        String elementName;
        String affiliation = null;
        String jid;
        User user = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    if (StringUtils.equalsIgnoreCase(elementName, "item")) {
                        affiliation = parser.getAttributeValue("", "affiliation");
                        jid = parser.getAttributeValue("", "jid");
                        user = TextUtils.isEmpty(jid) ? null : JidCreatorHelper.obtainUser(jid);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "item":
                            if (user == null) continue;
                            if (StringUtils.equalsIgnoreCase(affiliation, "owner")) {
                                conversationParticipants.setOwner(user);
                            } else {
                                conversationParticipants.addParticipant(user);
                            }
                            break;
                        case "query":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return conversationParticipants;
    }
}
