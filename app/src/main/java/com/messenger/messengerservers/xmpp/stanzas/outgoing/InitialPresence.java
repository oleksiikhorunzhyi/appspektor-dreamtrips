package com.messenger.messengerservers.xmpp.stanzas.outgoing;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class InitialPresence extends Stanza {

   public static final String ELEMENT = "presence";

   private String wvApiProtocolVersion;

   public InitialPresence(String wvApiProtocolVersion) {
      super();
      this.wvApiProtocolVersion = wvApiProtocolVersion;
   }

   @Override
   public CharSequence toXML() {
      XmlStringBuilder buf = new XmlStringBuilder();
      buf.halfOpenElement(ELEMENT);
      addCommonAttributes(buf);
      buf.attribute("wv-protocol-version", wvApiProtocolVersion);
      buf.rightAngleBracket();

      buf.closeElement(ELEMENT);

      return buf;
   }
}
