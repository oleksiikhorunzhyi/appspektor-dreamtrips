package com.worldventures.dreamtrips.social.service.friends.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.api.session.model.MutualFriends

import io.techery.mappery.MapperyContext

class MutualsConverter : Converter<MutualFriends, User.MutualFriends> {

   override fun sourceClass(): Class<MutualFriends> = MutualFriends::class.java

   override fun targetClass(): Class<User.MutualFriends> = User.MutualFriends::class.java

   override fun convert(context: MapperyContext, source: MutualFriends)
         : User.MutualFriends = User.MutualFriends(source.count())
}
