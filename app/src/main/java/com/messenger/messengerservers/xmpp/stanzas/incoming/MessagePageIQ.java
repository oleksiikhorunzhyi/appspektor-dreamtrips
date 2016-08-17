package com.messenger.messengerservers.xmpp.stanzas.incoming;

import com.messenger.messengerservers.model.Message;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

public class MessagePageIQ extends IQ {

   public static final String NAMESPACE = "urn:xmpp:archive";
   public static final String ELEMENT_CHAT = "chat";

   private List<Message> messages;
   private int loadedCount;

   public MessagePageIQ(List<Message> messages, int loadedCount) {
      super(ELEMENT_CHAT, NAMESPACE);
      this.messages = messages;
      this.loadedCount = loadedCount;
   }

   public List<Message> getMessages() {
      return messages;
   }

   public int getLoadedCount() {
      return loadedCount;
   }

   @Override
   protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
      return null;
   }
}
