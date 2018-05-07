package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.model.User.Relationship.INCOMING_REQUEST
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import javax.inject.Inject

class FriendsMainPresenter : Presenter<FriendsMainPresenter.View>() {

   @Inject lateinit var friendsInteractor: FriendsInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      friendsInteractor.requestsPipe
            .observeSuccessWithReplay()
            .map { it.result.filter { it.relationship == INCOMING_REQUEST }.count() }
            .compose(bindViewToMainComposer())
            .subscribe({ view.setRecentItems(it) })
      subscribeToErrorUpdates()
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private fun subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe { reportNoConnection() }
   }

   interface View : Presenter.View {
      fun setRecentItems(count: Int)
   }
}
