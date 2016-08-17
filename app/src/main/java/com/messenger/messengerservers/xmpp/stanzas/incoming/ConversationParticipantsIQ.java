package com.messenger.messengerservers.xmpp.stanzas.incoming;


import android.support.annotation.NonNull;

import com.messenger.messengerservers.model.ParticipantItem;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantsIQ extends IQ {

   public static final String NAMESPACE = "http://jabber.org/protocol/muc#admin";
   public static final String ELEMENT_QUERY = "query";

   private final List<ParticipantItem> participantItems = new ArrayList<>();

   public ConversationParticipantsIQ() {
      super(ELEMENT_QUERY, NAMESPACE);
   }

   public void addParticipantItem(ParticipantItem participantItem) {
      participantItems.add(participantItem);
   }

   @NonNull
   public List<ParticipantItem> getParticipantItems() {
      return participantItems;
   }

   @Override
   protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
      return null;
   }
}
