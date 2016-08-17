package com.messenger.messengerservers.xmpp.util;


import java.util.Random;

public final class StringGenerator {

   private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

   public static String getRandomString(final int sizeOfRandomString) {
      Random random = new Random();
      int length = ALLOWED_CHARACTERS.length();
      final StringBuilder sb = new StringBuilder(sizeOfRandomString);

      for (int i = 0; i < sizeOfRandomString; ++i) {
         sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(length)));
      }

      return sb.toString();
   }

}
