package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.PhotoTagHolder
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag
import icepick.State
import io.techery.janet.helper.ActionStateSubscriber
import rx.functions.Action1
import java.util.ArrayList
import java.util.HashSet
import javax.inject.Inject

class EditPhotoTagsPresenter(private val requestId: Long, private val suggestions: List<PhotoTag>,
                             private val photoTags: List<PhotoTag>, private val activeSuggestion: PhotoTag?) :
      Presenter<EditPhotoTagsPresenter.View>(), PhotoTagHolderManager.FriendRequestProxy {

   @Inject internal lateinit var friendsInteractor: FriendsInteractor

   @JvmField @State var locallyAddedTags = arrayListOf<PhotoTag>()
   @JvmField @State var locallyDeletedTags = arrayListOf<PhotoTag>()

   private lateinit var photoTagHolderManager: PhotoTagHolderManager

   override fun takeView(view: View) {
      super.takeView(view)
      photoTagHolderManager = PhotoTagHolderManager(view.photoTagHolder, account, account)
      photoTagHolderManager.setTagCreatedListener(this::onTagAdded)
      photoTagHolderManager.setTagDeletedListener(this::onTagDeleted)
      photoTagHolderManager.creationTagEnabled(true)
      photoTagHolderManager.setFriendRequestProxy(this)
   }

   fun onImageReady() {
      view.showImage(photoTagHolderManager)
      addTagsAndSuggestions()
   }

   private fun addTagsAndSuggestions() {
      addSuggestions()
      photoTagHolderManager.addExistsTagViews(photoTags)
      if (activeSuggestion != null) {
         photoTagHolderManager.addCreationTagBasedOnSuggestion(activeSuggestion)
      }
   }

   private fun addSuggestions() {
      val currentTags = HashSet<PhotoTag>()
      currentTags.addAll(photoTags)
      currentTags.addAll(locallyAddedTags)
      currentTags.removeAll(locallyDeletedTags)
      val notIntersectingSuggestions = PhotoTag.findSuggestionsNotIntersectingWithTags(suggestions, ArrayList(currentTags))
      photoTagHolderManager.addSuggestionTagViews(notIntersectingSuggestions) { photoTagHolderManager.addCreationTagBasedOnSuggestion(it) }
   }

   override fun requestFriends(query: String, page: Int, act: Action1<List<User>>) {
      friendsInteractor.friendsPipe
            .createObservable(GetFriendsCommand(query, page, PAGE_SIZE))
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetFriendsCommand>()
                  .onSuccess { getFriendsCommand ->
                     act.call(ArrayList(getFriendsCommand.result)
                           .filter { user -> !isUserExists(user) }.toList())
                  }
                  .onFail(this::handleError))
   }

   private fun isUserExists(user: User): Boolean {
      val containsOnServer = isContainUser(photoTags, user)
      val containsUserInLocallyAdded = isContainUser(locallyAddedTags, user)
      val containUserInDeleted = isContainUser(locallyDeletedTags, user)
      return containsUserInLocallyAdded || containsOnServer && !containUserInDeleted
   }

   private fun isContainUser(tagList: List<PhotoTag>, user: User) =
         tagList.map { if (it.user == null) User() else it.user }.contains(user)

   private fun onTagAdded(tag: PhotoTag) {
      locallyAddedTags.add(tag)
      locallyDeletedTags.remove(tag)
   }

   private fun onTagDeleted(tag: PhotoTag) {
      locallyDeletedTags.add(tag)
      locallyAddedTags.remove(tag)
      addSuggestions()
   }

   fun onDone() {
      locallyAddedTags.removeAll(photoTags)
      locallyAddedTags.addAll(photoTags)
      locallyAddedTags.removeAll(locallyDeletedTags)
      view.notifyAboutTags(requestId, locallyAddedTags, locallyDeletedTags)
   }

   interface View : RxView {

      val photoTagHolder: PhotoTagHolder

      fun notifyAboutTags(requestId: Long, addedTags: ArrayList<PhotoTag>, deletedTags: ArrayList<PhotoTag>)

      fun showImage(manager: PhotoTagHolderManager?)
   }

   companion object {

      private val PAGE_SIZE = 100
   }
}
