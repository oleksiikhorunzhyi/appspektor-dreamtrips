package com.worldventures.dreamtrips.wallet.util

import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.isValidCardName
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class WalletValidateHelperTest {

   @Test
   fun testCardNameValidator() {
      print("test testCardNameValidator")
      val nicknameTrue = listOf("First", "First ", "First Name", "First-Name", "First Name-",
            "-First Name", "-First-")

      val nicknameFalse = listOf("First#Name", "First@Name", " First Name", "-First Name-")

      for (nickname in nicknameTrue) {
         assertTrue(formatMessage(nickname), isValidCardName(nickname))
      }
      for (nickname in nicknameFalse) {
         assertFalse(formatMessage(nickname), isValidCardName(nickname))
      }
   }

   fun formatMessage(name: String) = "/$name/"
}