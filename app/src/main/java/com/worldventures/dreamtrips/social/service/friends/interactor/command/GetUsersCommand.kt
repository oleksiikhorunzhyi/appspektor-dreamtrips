package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.core.model.User

import javax.inject.Inject

import io.techery.janet.Janet
import io.techery.mappery.MapperyContext

abstract class GetUsersCommand : CommandWithError<List<User>>(), InjectableAction {

   @field:Inject lateinit var janet: Janet
   @field:Inject lateinit var mapperyContext: MapperyContext

   protected open fun convert(itemsToConvert: Iterable<*>): List<User> {
      return mapperyContext.convert(itemsToConvert, User::class.java)
   }
}
