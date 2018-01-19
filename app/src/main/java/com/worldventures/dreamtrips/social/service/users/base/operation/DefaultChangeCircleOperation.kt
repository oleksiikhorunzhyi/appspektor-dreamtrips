package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User

class DefaultChangeCircleOperation(val userId: Int, private val changeCircleAction: (MutableList<Circle>) -> List<Circle>)
   : BaseUserStorageOperation(false) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply {
      find { it.id == userId }?.copy()?.also {
         it.circles = changeCircleAction.invoke(it.circles.toMutableList())
         val position = indexOf(it)
         if (position != -1) set(position, it)
      }
   }
}
