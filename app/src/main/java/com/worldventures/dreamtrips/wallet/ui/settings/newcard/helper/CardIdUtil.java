package com.worldventures.dreamtrips.wallet.ui.settings.newcard.helper;

public final class CardIdUtil {

   public static String pushZeroToSmartCardId(String cardId) {
      if(cardId == null) return "";
      StringBuilder cardIdBuilder = new StringBuilder("0000000000");
      cardIdBuilder.replace(
            cardIdBuilder.length() - cardId.length(),
            cardIdBuilder.length(),
            cardId
      );
      return cardIdBuilder.toString();
   }
}
