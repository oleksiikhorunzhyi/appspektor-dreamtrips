package com.worldventures.wallet.ui.settings.general.newcard.helper;

// TODO: 12/17/17 WTF ?
public final class CardIdUtil {

   private CardIdUtil() {
   }

   public static String pushZeroToSmartCardId(String cardId) {
      if (cardId == null) {
         return "";
      }
      StringBuilder cardIdBuilder = new StringBuilder("0000000000");
      cardIdBuilder.replace(
            cardIdBuilder.length() - cardId.length(),
            cardIdBuilder.length(),
            cardId
      );
      return cardIdBuilder.toString();
   }
}
