package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.messenger.delegate.StartChatDelegate
import com.messenger.ui.activity.MessengerActivity
import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import javax.inject.Inject

abstract class BaseUserListPresenter<V : BaseUserListPresenter.View> : Presenter<V>() {

   @Inject lateinit var startChatDelegate: StartChatDelegate

   override fun takeView(view: V) {
      super.takeView(view)
      subscribeOnStorage()
      subscribeOnErrors()
      if (isNeedPreload()) {
         reload()
      }
   }

   protected abstract fun subscribeOnStorage()

   protected abstract fun subscribeOnErrors()

   protected open fun finishUpdateStorage(resultCommand: BaseUserStorageCommand) {
      view.finishLoading()
      view.refreshUsers(resultCommand.getStorageItems(), resultCommand.isNoMoreItems())
   }

   override fun handleError(action: Any?, error: Throwable?) {
      view.finishLoading()
      super.handleError(action, error)
   }

   fun openPrefs(user: User) {
      view.openFriendPrefs(UserBundle(user))
   }

   fun startChat(user: User) {
      startChatDelegate.startSingleChat(user) {
         MessengerActivity.startMessengerWithConversation(activityRouter.context, it.id)
      }
   }

   open fun reload() {
      view.startLoading()
      loadUsers(true)
   }

   open fun loadNext() {
      view.startLoading()
      loadUsers(false)
   }

   protected abstract fun loadUsers(reload: Boolean)

   protected open fun isNeedPreload() = true

   fun userClicked(user: User) {
      view.openUser(UserBundle(user))
   }

   protected fun onCirclesError(commandWithError: CommandWithError<*>, throwable: Throwable) {
      view.hideBlockingProgress()
      handleError(commandWithError, throwable)
   }

   interface View : Presenter.View, BlockingProgressView {

      fun startLoading()

      fun finishLoading()

      fun refreshUsers(users: List<User>?, noMoreItems: Boolean)

      fun openFriendPrefs(userBundle: UserBundle)

      fun showAddFriendDialog(circles: List<Circle>?, selectAction: (Circle) -> Unit)

      fun openUser(userBundle: UserBundle)
   }
}
