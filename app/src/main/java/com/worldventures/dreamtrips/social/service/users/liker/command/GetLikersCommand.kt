package com.worldventures.dreamtrips.social.service.users.liker.command

import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.likes.GetLikersHttpAction
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class GetLikersCommand(val feedEntity: FeedEntity, val page: Int, val perPage: Int) : GetUsersCommand() {

   @Inject lateinit var sessionHolder: SessionHolder

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<User>>) {
      janet.createPipe(GetLikersHttpAction::class.java)
            .createObservableResult(GetLikersHttpAction(feedEntity.uid, page, perPage))
            .map { it.response() }
            .map { this.convert(it) }
            .doOnNext { this.onLikersLoaded(it) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun onLikersLoaded(users: List<User>?) {
      feedEntity.firstLikerName = if (users == null || users.isEmpty()) null
      else users.firstOrNull { it.id != sessionHolder.get().get().user().id }?.fullName
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_people_who_liked
}
