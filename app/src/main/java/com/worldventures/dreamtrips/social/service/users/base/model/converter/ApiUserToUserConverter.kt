package com.worldventures.dreamtrips.social.service.users.base.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.model.User

import io.techery.mappery.MapperyContext

import com.worldventures.dreamtrips.api.likes.model.User as ApiUser

class ApiUserToUserConverter : Converter<ApiUser, User> {

   override fun sourceClass() = ApiUser::class.java

   override fun targetClass() = User::class.java

   override fun convert(context: MapperyContext, source: ApiUser) =
         User().apply {
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
