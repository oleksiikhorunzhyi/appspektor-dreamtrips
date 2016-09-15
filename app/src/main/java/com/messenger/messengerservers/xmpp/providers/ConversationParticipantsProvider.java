package com.messenger.messengerservers.xmpp.providers;

import android.text.TextUtils;

import com.messenger.messengerservers.model.ImmutableParticipantItem;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationParticipantsIQ;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ConversationParticipantsProvider extends IQProvider<ConversationParticipantsIQ> {
   private static final String QUERY = "query";
   private static final String PARTICIPANT_ITEM = "item";
   private static final String PARTICIPANT_JID = "jid";
   private static final String PARTICIPANT_AFFILIATION = "affiliation";

   @Override
   public ConversationParticipantsIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
      ConversationParticipantsIQ conversationParticipantsIQ = new ConversationParticipantsIQ();
      boolean done = false;

      while (!done) {
         int eventType = parser.next();
         String elementName = parser.getName();
         switch (eventType) {
            case XmlPullParser.START_TAG:
               switch (elementName) {
                  case PARTICIPANT_ITEM:
                     String jid = parser.getAttributeValue("", PARTICIPANT_JID);
                     if (TextUtils.isEmpty(jid)) break;
                     String affiliation = parser.getAttributeValue("", PARTICIPANT_AFFILIATION);
                     String participantId = JidCreatorHelper.obtainId(jid);
                     conversationParticipantsIQ.addParticipantItem(ImmutableParticipantItem.builder()
                           .affiliation(affiliation)
                           .userId(participantId)
                           .build());
                     break;
               }
            case XmlPullParser.END_TAG:
               switch (elementName) {
                  case QUERY:
                     done = true;
               }
         }
      }
      return conversationParticipantsIQ;
   }
}
