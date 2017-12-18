package com.worldventures.wallet.service.command.http

import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.GetTermsAndConditionsHttpAction
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.TermsAndConditions
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class FetchTermsAndConditionsCommand : Command<TermsAndConditions>(), InjectableAction, CachedAction<TermsAndConditions> {

   @Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<TermsAndConditions>) {
      janet.createPipe(GetTermsAndConditionsHttpAction::class.java)
            .createObservableResult(GetTermsAndConditionsHttpAction())
            .map { action -> convertResponse(action.response()) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun convertResponse(response: com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions): TermsAndConditions {
      return TermsAndConditions(tacVersion = response.version().toString(), url = response.url())
   }

   override fun getCacheData(): TermsAndConditions = result

   override fun onRestore(holder: ActionHolder<*>, cache: TermsAndConditions) {

   }

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build()
   }

}
