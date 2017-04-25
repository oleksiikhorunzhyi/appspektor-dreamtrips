package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto
import java.io.File

class TestSmartCardUsurPhoto: SmartCardUserPhoto {
   override fun original(): File? = null

   override fun photoUrl(): String = ""
}

