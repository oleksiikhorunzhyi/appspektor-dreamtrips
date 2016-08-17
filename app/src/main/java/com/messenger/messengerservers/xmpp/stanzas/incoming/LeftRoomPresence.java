package com.messenger.messengerservers.xmpp.stanzas.incoming;

import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Stanza;

public class LeftRoomPresence extends Stanza {
   public static final StanzaTypeFilter LEAVE_PRESENCE_FILTER = new StanzaTypeFilter(LeftRoomPresence.class);
   public static final String TYPE = "leave";

   @Override
   public CharSequence toXML() {
      return null;
   }
}
