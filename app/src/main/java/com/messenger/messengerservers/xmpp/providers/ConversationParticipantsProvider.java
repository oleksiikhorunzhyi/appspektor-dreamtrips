package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationParticipantsIQ;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import timber.log.Timber;

public class ConversationParticipantsProvider extends IQProvider<ConversationParticipantsIQ> {

    public ConversationParticipantsProvider() {
    }

    @Override
    public ConversationParticipantsIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ConversationParticipantsIQ conversationParticipantsIQ = new ConversationParticipantsIQ();

        String elementName;
        String affiliation = null;
        String participantId = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "item":
                            affiliation = parser.getAttributeValue("", "affiliation");
                            String jid = parser.getAttributeValue("", "jid");
                            participantId = TextUtils.isEmpty(jid) ? null : JidCreatorHelper.obtainId(jid);
                            break;
                    }
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "item":
                            if (participantId == null) continue;
                            Participant participant = new Participant(participantId, affiliation.toLowerCase(), null);
                            conversationParticipantsIQ.addParticipant(participant);
                            break;
                        case "query":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return conversationParticipantsIQ;
    }
}
