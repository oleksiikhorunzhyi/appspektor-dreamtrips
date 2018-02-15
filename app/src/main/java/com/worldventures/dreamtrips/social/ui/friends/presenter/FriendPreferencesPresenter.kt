package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor
import com.worldventures.dreamtrips.social.service.profile.command.AddFriendToCircleCommand
import com.worldventures.dreamtrips.social.service.profile.command.RemoveFriendFromCircleCommand
import com.worldventures.dreamtrips.social.service.profile.model.FriendGroupRelation
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate.State
import io.techery.janet.helper.ActionStateSubscriber
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class FriendPreferencesPresenter(userBundle: UserBundle) : Presenter<FriendPreferencesPresenter.View>() {

   @Inject lateinit var circlesInteractor: CirclesInteractor
   @Inject lateinit var profileInteractor: ProfileInteractor

   val friend: User = userBundle.user

   override fun takeView(view: View?) {
      super.takeView(view)
      subscribeCircles()
      updateCircles()
   }

   private fun subscribeCircles() {
      circlesInteractor.pipe
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetCirclesCommand>()
                  .onStart { view.showBlockingProgress() }
                  .onSuccess {
                     view.addItems(it.result.map { FriendGroupRelation(it, friend) }.toList())
                     view.hideBlockingProgress()
                  }
                  .onFail({ command, error ->
                     view.hideBlockingProgress()
                     handleError(command, error)
                  }))
   }

   private fun updateCircles() {
      circlesInteractor.pipe.send(GetCirclesCommand())
   }

   fun onRelationshipChanged(circle: Circle, state: State) {
      when (state) {
         State.ADDED -> {
            profileInteractor.addFriendToCirclePipe.send(AddFriendToCircleCommand(circle, friend))
            friend.circles.add(circle)
         }
         State.REMOVED -> {
            profileInteractor.removeFriendFromCirclePipe
                  .send(RemoveFriendFromCircleCommand(circle, friend))
         }
      }
   }

   interface View : Presenter.View, BlockingProgressView {
      fun addItems(circles: List<FriendGroupRelation>)
   }
}
