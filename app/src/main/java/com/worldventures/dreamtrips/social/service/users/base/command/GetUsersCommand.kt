package com.worldventures.dreamtrips.social.service.users.base.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.User
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Janet
import io.techery.mappery.MapperyContext
import javax.inject.Inject

abstract class GetUsersCommand : CommandWithError<List<User>>(), InjectableAction {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mapperyContext: MapperyContext

   protected open fun convert(itemsToConvert: Iterable<*>) = mapperyContext.convert(itemsToConvert, User::class.java)

}
