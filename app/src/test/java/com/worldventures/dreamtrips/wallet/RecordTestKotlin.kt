package com.worldventures.dreamtrips.wallet

import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecordTestKotlin {

   @Test
   fun paymentCardNicknameValidationTest() {
      val nicknameTrue = listOf("First", "First ", "First Name", " First Name", "First-Name", "First Name-",
            "-First Name", "-First-")

      val nicknameFalse = listOf("First#Name", "First@Name", "-First Name-")

      for (nickname in nicknameTrue) {
         assertTrue(WalletValidateHelper.isValidCardName(nickname))
      }
      for (nickname in nicknameFalse) {
         assertFalse(WalletValidateHelper.isValidCardName(nickname))
      }
   }
}