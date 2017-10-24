package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class WalletProfileUtilsTest {

   @Test
   fun equalsPhoneTest() {
      val phone = SmartCardUserPhone.of("+38", "0676776767")

      assertTrue(equalsPhone(phone, "+38", "0676776767"))
      assertFalse(equalsPhone(phone, "+38", "00000000"))
      assertFalse(equalsPhone(phone, "+1", "0676776767"))
      assertFalse(equalsPhone(null, "+38", "0676776767"))

      assertTrue("phone doesn't exist without any part", equalsPhone(null, "+38", ""))
      assertTrue("phone doesn't exist without any part", equalsPhone(null, "", "060603"))
      assertTrue("phone doesn't exist without any part", equalsPhone(null, "", ""))
      assertFalse("phone doesn't exist without any part", equalsPhone(phone, "", ""))
      assertFalse("phone doesn't exist without any part", equalsPhone(phone, "+38", ""))
      assertFalse("phone doesn't exist without any part", equalsPhone(phone, "", "543252463"))
   }
}
