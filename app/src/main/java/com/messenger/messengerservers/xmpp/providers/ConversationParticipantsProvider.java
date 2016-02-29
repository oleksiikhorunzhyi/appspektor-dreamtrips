package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.packets.ConversationParticipants;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ConversationParticipantsProvider extends IQProvider<ConversationParticipants> {

    private final String userId;

    public ConversationParticipantsProvider(String userJid) {
        this.userId = JidCreatorHelper.obtainId(userJid);
    }

    @Override
    public ConversationParticipants parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ConversationParticipants conversationParticipants = new ConversationParticipants();

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

                            if (TextUtils.equals(affiliation.toLowerCase(), Participant.Affiliation.OWNER)) {
                                conversationParticipants.setOwner(participant);
                            } else {
                                conversationParticipants.addParticipant(participant);
                            }
                            break;
                        case "query":
                            boolean isOwner = conversationParticipants.getOwner() != null && TextUtils.equals(conversationParticipants.getOwner().getUserId(), userId);
                            boolean isMember = Queryable.from(conversationParticipants.getParticipants()).map((elem, idx) -> elem.getUserId()).contains(userId);
                            conversationParticipants.setAbandoned(!isOwner && !isMember);
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return conversationParticipants;
    }
}
