package com.worldventures.dreamtrips.social.service.friends.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.api.friends.model.FriendProfile

import io.techery.mappery.MapperyContext

class FriendProfileToUserConverter : Converter<FriendProfile, User> {

   override fun sourceClass(): Class<FriendProfile> = FriendProfile::class.java

   override fun targetClass(): Class<User> = User::class.java

   override fun convert(context: MapperyContext, source: FriendProfile): User {
      return User().apply {
         id = source.id()
         firstName = source.firstName()
         lastName = source.lastName()
         username = source.username()
         badges = source.badges()
         location = source.location()
         company = source.company()
         avatar = context.convert(source.avatar(), User.Avatar::class.java)
         relationship = source.relationship()?.let { context.convert(it, User.Relationship::class.java) }
         circles = source.circles()?.let { context.convert(it, Circle::class.java) } ?: mutableListOf()
         mutualFriends = source.mutuals()?.let { context.convert(it, User.MutualFriends::class.java) }
      }
   }
}
