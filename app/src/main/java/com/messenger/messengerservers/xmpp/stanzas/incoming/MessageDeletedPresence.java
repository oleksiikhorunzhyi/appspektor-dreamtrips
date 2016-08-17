package com.messenger.messengerservers.xmpp.stanzas.incoming;

import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Stanza;

public class MessageDeletedPresence extends Stanza {

   public static final StanzaTypeFilter DELETED_PRESENCE_FILTER = new StanzaTypeFilter(MessageDeletedPresence.class);
   public static final String TYPE = "deleted-messages";

   @Override
   public CharSequence toXML() {
      return null;
   }
}
