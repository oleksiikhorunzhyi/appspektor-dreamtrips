package com.worldventures.dreamtrips.social.service.users.base.model

class RequestHeaderModel(val name: String?, val isAdvanced: Boolean) {

   var count: Int = 0

   constructor(name: String?) : this(name, false)

   override fun equals(other: Any?) = (other as? RequestHeaderModel)?.let {
      name == it.name && count == it.count && isAdvanced == it.isAdvanced
   } ?: false

   override fun hashCode() = name?.hashCode() ?: 0 + count
}
