package com.worldventures.dreamtrips.social.ui.tripsimages.model

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity

abstract class BaseMediaEntity<T : FeedEntity> (var item: T, val type: MediaEntityType) {

   @SuppressWarnings("ReturnCount", "UnsafeCast")
   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as BaseMediaEntity<*>

      if (item != other.item) return false
      if (type != other.type) return false

      return true
   }

   override fun hashCode() = 31 * item.hashCode() + type.hashCode()
}
