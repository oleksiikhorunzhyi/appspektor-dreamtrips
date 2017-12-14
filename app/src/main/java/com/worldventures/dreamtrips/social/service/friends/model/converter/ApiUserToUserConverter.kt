package com.worldventures.dreamtrips.social.service.friends.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.model.User

import io.techery.mappery.MapperyContext

class ApiUserToUserConverter : Converter<com.worldventures.dreamtrips.api.likes.model.User, User> {

   override fun sourceClass(): Class<com.worldventures.dreamtrips.api.likes.model.User> {
      return com.worldventures.dreamtrips.api.likes.model.User::class.java
   }

   override fun targetClass(): Class<User> = User::class.java

   override fun convert(context: MapperyContext, source: com.worldventures.dreamtrips.api.likes.model.User): User {
      return User().apply {
         id = source.id()
         firstName = source.firstName()
         lastName = source.lastName()
         username = source.userName()
         badges = source.badges()
         location = source.location()
         company = source.company()
         relationship = context.convert(source.relationship(), User.Relationship::class.java)
         avatar = context.convert(source.avatar(), User.Avatar::class.java)
         relationship = context.convert(source.relationship(), User.Relationship::class.java)
         mutualFriends = source.mutuals()?.let { context.convert(it, User.MutualFriends::class.java) }
      }
   }
}
