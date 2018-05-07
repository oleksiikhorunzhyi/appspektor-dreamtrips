package com.worldventures.dreamtrips.social.service.users.base.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate

import io.techery.mappery.MapperyContext

class FriendCandidateToUserConverter : Converter<FriendCandidate, User> {

   override fun sourceClass(): Class<FriendCandidate> = FriendCandidate::class.java

   override fun targetClass(): Class<User> = User::class.java

   override fun convert(context: MapperyContext, source: FriendCandidate) =
         User().apply {
            id = source.id()
            firstName = source.firstName()
            lastName = source.lastName()
            username = source.username()
            location = source.location()
            company = source.company()
            badges = source.badges()
            avatar = context.convert(source.avatar(), User.Avatar::class.java)
            mutualFriends = source.mutuals()?.let { context.convert(it, User.MutualFriends::class.java) }
            relationship = source.relationship()?.let { context.convert(it, User.Relationship::class.java) }
         }

}
