package com.worldventures.dreamtrips.wallet.util


import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardName
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class WalletValidateHelperTest {

   @Test
   fun testCardNameValidator() {
      print("test testCardNameValidator")
      assertTrue("TestName", validateCardName("TestName"))
      assertTrue("TestName 2", validateCardName("TestName 2"))
      assertTrue("TestName-2", validateCardName("TestName-2"))
      assertTrue("Tes-Nam-tes", validateCardName("Tes-Nam-tes"))
      assertTrue("Test Name-1", validateCardName("Test-Name-1"))
      assertTrue("Test2-Name", validateCardName("Test2-Name"))
      assertTrue("-TestName", validateCardName("-TestName"))
      assertTrue("2-TestName", validateCardName("2-TestName"))
      assertTrue("test-test-t", validateCardName("test-test-t")) // 11 chars

      assertFalse("test-test-tt", validateCardName("test-test-tt"))// 12 chars
      assertFalse("First symbol is space", validateCardName(" TestName"))
      assertFalse("Last symbol is space", validateCardName("TestName "))
      assertFalse("Last symbol is dash", validateCardName("TestName-"))
      assertFalse("Name contains only dashes", validateCardName("- -"))
   }
}