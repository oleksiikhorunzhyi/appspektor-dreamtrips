package com.worldventures.dreamtrips.qa

import com.google.gson.Gson
import java.io.File
import java.io.FileReader

interface QaConfigLoader {

   val config: QaConfig

   class FileQaConfigLoader(val file: File, val gson: Gson) : QaConfigLoader {
      override val config: QaConfig by lazy {
         gson.fromJson<QaConfig>(FileReader(file), QaConfig::class.java)
      }
   }

}
