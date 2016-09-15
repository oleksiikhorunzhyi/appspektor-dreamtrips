package com.messenger.messengerservers.xmpp.stanzas;

public class ClearChatIQ extends BaseClearChatIQ {
   public static final String ELEMENT_CLEAR_DATE = "clear-date";
   private final long clearDate;

   public ClearChatIQ(String chatId, long clearDate) {
      super(chatId, null);
      this.clearDate = clearDate;
   }

   public ClearChatIQ(String chatId, String userJid, long clearDate) {
      super(chatId, userJid);
      this.clearDate = clearDate;
   }

   @Override
   protected void addChildElement(IQChildElementXmlStringBuilder xml) {
      xml.openElement(ELEMENT_CLEAR_DATE).append(Long.toString(clearDate)).closeElement(ELEMENT_CLEAR_DATE);
   }

   public long getClearDate() {
      return clearDate;
   }
}
