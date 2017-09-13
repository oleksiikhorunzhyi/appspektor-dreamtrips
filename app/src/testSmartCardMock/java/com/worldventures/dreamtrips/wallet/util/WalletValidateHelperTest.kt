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

   @Test
   fun testProfileLastNameWithSuffixValidator() {
      print("test testProfileLastNameWithSuffixValidator")
      val lastNameTrue = listOf("Pichai", "Pichai II", "Pichai-II", "Pichai Jr.", "Pichai Sr", "Pichai-asd-asad", "Pichai - Sr.", "Pichai - Sr")

      val lastNameFalse = listOf("#Pichai", "Pichai_II", "Pichai, II", "Pichai-II-", "-Pichai II", "-Pichai", "Pichai - Sr-.", "Pichai, II Sn Sr", "Pichai Inasjndkajsndkjnaskjd")

      for (lastName in lastNameTrue) {
         assertTrue(WalletValidateHelper.isValidLastName(lastName))
      }
      for (lastName in lastNameFalse) {
         assertFalse(WalletValidateHelper.isValidLastName(lastName))
      }
   }

   @Test
   fun testProfileFirstNameWithSuffixValidator() {
      print("test testProfileFirstNameWithSuffixValidator")
      val firstNameTrue = listOf("Sundararajan", "Sundararajan-Ruo", "    Sundararajan-Ruo    ", "  Sundararajan-s-s-s")

      val firstNameFalse = listOf("#Sundararajan", "Sundararajan---", "----Sundararajan----")

      for (firstName in firstNameTrue) {
         assertTrue(WalletValidateHelper.isValidFirstName(firstName))
      }
      for (firstName in firstNameFalse) {
         assertFalse(WalletValidateHelper.isValidFirstName(firstName))
      }
   }
}