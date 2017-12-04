package com.worldventures.dreamtrips.social.ui.reptools.presenter

import com.worldventures.core.model.ShareType
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WebViewFragmentPresenter
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.dreamtrips.social.ui.reptools.service.SuccessStoriesInteractor
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.AdobeFavoriteSuccessStoryAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.AdobeViewSuccessStoryAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveSuccessStoryAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ShareSuccessStoryAction
import com.worldventures.dreamtrips.social.ui.reptools.service.command.LikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.ui.reptools.service.command.UnlikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.util.event_delegate.StoryLikedEventDelegate
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class SuccessStoryDetailsPresenter(private val successStory: SuccessStory, url: String) : WebViewFragmentPresenter<SuccessStoryDetailsPresenter.View>(url) {

   @field:Inject lateinit var storyLikedEventDelegate: StoryLikedEventDelegate
   @field:Inject lateinit var successStoriesInteractor: SuccessStoriesInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      analyticsInteractor.analyticsActionPipe().send(ApptentiveSuccessStoryAction())
      analyticsInteractor.analyticsActionPipe().send(AdobeViewSuccessStoryAction(successStory.url))
   }

   fun like(successStory: SuccessStory) {
      if (successStory.isLiked) {
         successStoriesInteractor.unlikeSuccessStoryPipe
               .createObservable(UnlikeSuccessStoryCommand(successStory.id))
               .compose(bindViewToMainComposer())
               .subscribe(ActionStateSubscriber<UnlikeSuccessStoryCommand>()
                     .onSuccess { onLiked() }
                     .onFail(this::handleError))
      } else {
         successStoriesInteractor.likeSuccessStoryPipe
               .createObservable(LikeSuccessStoryCommand(successStory.id))
               .compose(bindViewToMainComposer())
               .subscribe(ActionStateSubscriber<LikeSuccessStoryCommand>()
                     .onSuccess { onLiked() }
                     .onFail(this::handleError))
      }
      analyticsInteractor.analyticsActionPipe().send(ApptentiveSuccessStoryAction())
      analyticsInteractor.analyticsActionPipe().send(AdobeFavoriteSuccessStoryAction(successStory.url))
   }

   private fun onLiked() = view.likeRequestSuccess()

   fun onStoryLiked(successStory: SuccessStory) {
      view.updateStoryLike(successStory.isLiked)
      storyLikedEventDelegate.post(successStory)
   }

   fun share() = view.showShareDialog()

   fun onShare(@ShareType type: String, successStory: SuccessStory) {
      view.openShare(successStory.sharingUrl, type)
      analyticsInteractor.analyticsActionPipe().send(ShareSuccessStoryAction(type, successStory.url))
   }

   interface View : WebViewFragmentPresenter.View {

      fun showShareDialog()

      fun likeRequestSuccess()

      fun openShare(url: String, @ShareType type: String)

      fun updateStoryLike(isLiked: Boolean)
   }

}
