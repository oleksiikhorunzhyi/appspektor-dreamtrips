package com.worldventures.dreamtrips.social.service.profile.model

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User

class FriendGroupRelation(val circle: Circle, val friend: User) {
   val isFriendInCircle: Boolean get() = friend.circles.contains(circle)
}
