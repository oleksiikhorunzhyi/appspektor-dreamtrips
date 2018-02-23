package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.Circle
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.circles.GetCirclesHttpAction
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class GetCirclesCommand : CommandWithError<List<Circle>>(), InjectableAction, CachedAction<List<Circle>> {

   @field:Inject lateinit var janet: Janet
   @field:Inject lateinit var mapperyContext: MapperyContext

   private var cachedData: List<Circle>? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Circle>>) {
      if (cachedData == null || cachedData?.isEmpty() != false) {
         janet.createPipe(GetCirclesHttpAction::class.java, Schedulers.io())
               .createObservableResult(GetCirclesHttpAction())
               .map { mapperyContext.convert(it.response(), Circle::class.java) }
               .subscribe(callback::onSuccess, callback::onFail)
      } else {
         callback.onSuccess(cachedData)
      }
   }

   override fun getFallbackErrorMessage(): Int = R.string.error_fail_to_load_circles

   override fun getCacheData(): List<Circle> = result?.let { ArrayList(it) } ?: ArrayList()

   override fun onRestore(holder: ActionHolder<*>, cache: List<Circle>) {
      cachedData = cache
   }

   override fun getCacheOptions() = CacheOptions()
}
