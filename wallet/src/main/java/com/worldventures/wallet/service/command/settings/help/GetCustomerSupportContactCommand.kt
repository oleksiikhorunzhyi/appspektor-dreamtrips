package com.worldventures.wallet.service.command.settings.help

import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.api.smart_card.documents.customer_support.GetCustomerSupportContactsHttpAction
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.settings.customer_support.Contact
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import rx.Observable.just
import javax.inject.Inject

@CommandAction
class GetCustomerSupportContactCommand : Command<Contact>(), InjectableAction, CachedAction<Contact> {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mapperyContext: MapperyContext

   private var cachedResult: Contact? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Contact>) {
      if (!needApiRequest()) {
         callback.onSuccess(cachedResult)
         return
      }

      janet.createPipe(GetCustomerSupportContactsHttpAction::class.java)
            .createObservableResult(GetCustomerSupportContactsHttpAction())
            .map { mapperyContext.convert(it.response(), Contact::class.java) }
            .flatMap { contacts ->
               return@flatMap if (contacts != null && contacts.isNotEmpty()) {
                  just(contacts[0])
               } else {
                  Observable.error(NullPointerException("Server response must contain at least one contact"))
               }
            }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun needApiRequest(): Boolean {
      return cachedResult == null
   }

   override fun getCacheData(): Contact? = result

   override fun onRestore(holder: ActionHolder<*>, cache: Contact) {
      cachedResult = cache
   }

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .saveToCache(needApiRequest())
            .build()
   }
}
