package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.SearchFriendsHttpAction
import com.worldventures.dreamtrips.api.friends.model.ImmutableSearchParams
import com.worldventures.dreamtrips.api.friends.model.SearchParams
import io.techery.janet.Command

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetSearchUsersCommand(val query: String, val page: Int, val perPage: Int) : GetUsersCommand() {

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<User>>) {
      janet.createPipe(SearchFriendsHttpAction::class.java)
            .createObservableResult(SearchFriendsHttpAction(provideSearchParams()))
            .map { it.response() }
            .map { this.convert(it) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun provideSearchParams(): SearchParams {
      return ImmutableSearchParams.builder()
            .page(page)
            .perPage(perPage)
            .query(query)
            .build()
   }

   override fun getFallbackErrorMessage(): Int = R.string.error_failed_to_load_users
}
