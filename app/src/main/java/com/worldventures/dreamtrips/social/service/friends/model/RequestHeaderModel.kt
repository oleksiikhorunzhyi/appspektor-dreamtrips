package com.worldventures.dreamtrips.social.service.friends.model

class RequestHeaderModel constructor(val name: String?, val isAdvanced: Boolean) {

   var count: Int = 0

   constructor(name: String?) : this(name, false)
}
