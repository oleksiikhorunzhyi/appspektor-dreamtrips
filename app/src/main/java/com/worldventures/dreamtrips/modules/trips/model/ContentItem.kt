package com.worldventures.dreamtrips.modules.trips.model

import java.io.Serializable

class ContentItem : Serializable {

   var description: String? = null
      get() = field?.replace("\n".toRegex(), "")?.replace("\t".toRegex(), "")
   var language: String? = null
   var name: String? = null
   var tags: List<String>? = null

   companion object {
      const val serialVersionUID = 138L
   }
}
