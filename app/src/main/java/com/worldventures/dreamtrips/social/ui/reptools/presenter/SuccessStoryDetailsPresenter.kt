package com.worldventures.dreamtrips.social.ui.reptools.presenter

import com.worldventures.core.model.ShareType
import com.worldventures.dreamtrips.social.service.reptools.SuccessStoriesInteractor
import com.worldventures.dreamtrips.social.service.reptools.command.LikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.service.reptools.command.UnlikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WebViewFragmentPresenter
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.AdobeViewSuccessStoryAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveSuccessStoryAction
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ShareSuccessStoryAction
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

class SuccessStoryDetailsPresenter(private var successStory: SuccessStory, url: String) : WebViewFragmentPresenter<SuccessStoryDetailsPresenter.View>(url) {

   @Inject lateinit var successStoriesInteractor: SuccessStoriesInteractor

   override fun takeView(view: View) {
      super.takeView(view)
      view.updateLikeStatus(successStory.isLiked)
      analyticsInteractor.analyticsActionPipe().send(ApptentiveSuccessStoryAction())
      analyticsInteractor.analyticsActionPipe().send(AdobeViewSuccessStoryAction(successStory.url))
      subscribeToLikeStatusUpdate()
   }

   private fun subscribeToLikeStatusUpdate() {
      successStoriesInteractor.updateLikeStatusPipe
            .observeSuccess()
            .map { it.result.first { it.id == successStory.id } }
            .compose(bindViewToMainComposer())
            .subscribe {
               successStory = it
               view.likeRequestSuccess(successStory.isLiked)
               view.updateLikeStatus(successStory.isLiked)
            }
   }

   fun like() {
      if (successStory.isLiked) {
         successStoriesInteractor.unlikeSuccessStoryPipe
               .createObservable(UnlikeSuccessStoryCommand(successStory.id))
               .compose(bindViewToMainComposer())
               .subscribe(ActionStateSubscriber<UnlikeSuccessStoryCommand>().onFail(this::handleError))
      } else {
         successStoriesInteractor.likeSuccessStoryPipe
               .createObservable(LikeSuccessStoryCommand(successStory.id))
               .compose(bindViewToMainComposer())
               .subscribe(ActionStateSubscriber<LikeSuccessStoryCommand>().onFail(this::handleError))
      }
   }

   fun onUpdateAuthorRequired() = view.updateStoryTitle(successStory.author)

   fun onFullscreenPressed() = view.openFullscreen(successStory)

   fun onShare(@ShareType type: String) {
      view.openShare(successStory.sharingUrl, type)
      analyticsInteractor.analyticsActionPipe().send(ShareSuccessStoryAction(type, successStory.url))
   }

   interface View : WebViewFragmentPresenter.View {
      fun updateStoryTitle(author: String)

      fun likeRequestSuccess(isLiked: Boolean)

      fun updateLikeStatus(isLike: Boolean)

      fun openShare(url: String, @ShareType type: String)

      fun openFullscreen(story: SuccessStory)
   }

}
