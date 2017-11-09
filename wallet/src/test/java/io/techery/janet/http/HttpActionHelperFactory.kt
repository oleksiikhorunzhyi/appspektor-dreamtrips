package io.techery.janet.http

import io.techery.janet.HttpActionService

@Suppress("UNUSED")
class HttpActionHelperFactory : HttpActionService.ActionHelperFactory {

   private val factoryList = listOf(HttpActionHelperFactoryForDreamTripsApi(), HttpActionHelperFactoryWalletModule())

   override fun make(actionClass: Class<*>?): HttpActionService.ActionHelper<*>? {
      factoryList
            .mapNotNull { it.make(actionClass) }
            .forEach { return it }
      return null
   }
}