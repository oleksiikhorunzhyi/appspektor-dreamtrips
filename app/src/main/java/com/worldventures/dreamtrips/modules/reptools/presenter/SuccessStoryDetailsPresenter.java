package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.techery.spares.utils.delegate.StoryLikedEventDelegate;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.service.SuccessStoriesInteractor;
import com.worldventures.dreamtrips.modules.reptools.service.analytics.AdobeFavoriteSuccessStoryAction;
import com.worldventures.dreamtrips.modules.reptools.service.analytics.AdobeViewSuccessStoryAction;
import com.worldventures.dreamtrips.modules.reptools.service.analytics.ApptentiveSuccessStoryAction;
import com.worldventures.dreamtrips.modules.reptools.service.analytics.ShareSuccessStoryAction;
import com.worldventures.dreamtrips.modules.reptools.service.command.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.service.command.UnlikeSuccessStoryCommand;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SuccessStoryDetailsPresenter extends WebViewFragmentPresenter<SuccessStoryDetailsPresenter.View> {

   private SuccessStory successStory;

   @Inject StoryLikedEventDelegate storyLikedEventDelegate;
   @Inject SuccessStoriesInteractor successStoriesInteractor;

   public SuccessStoryDetailsPresenter(SuccessStory story, String url) {
      super(url);
      this.successStory = story;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      analyticsInteractor.analyticsActionPipe().send(ApptentiveSuccessStoryAction.view());
      analyticsInteractor.analyticsActionPipe().send(new AdobeViewSuccessStoryAction(successStory.getUrl()));
   }

   public void like(SuccessStory successStory) {
      if (successStory.isLiked()) {
         analyticsInteractor.analyticsActionPipe().send(ApptentiveSuccessStoryAction.unlike());
         successStoriesInteractor.unlikeSuccessStoryPipe()
               .createObservable(new UnlikeSuccessStoryCommand(successStory.getId()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<UnlikeSuccessStoryCommand>()
                     .onSuccess(command -> onLiked())
                     .onFail(this::handleError));
      } else {
         analyticsInteractor.analyticsActionPipe().send(ApptentiveSuccessStoryAction.like());
         successStoriesInteractor.likeSuccessStoryPipe()
               .createObservable(new LikeSuccessStoryCommand(successStory.getId()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<LikeSuccessStoryCommand>()
                     .onSuccess(command -> onLiked())
                     .onFail(this::handleError));
      }
      analyticsInteractor.analyticsActionPipe().send(new AdobeFavoriteSuccessStoryAction(successStory.getUrl()));
   }

   private void onLiked() {
      view.likeRequestSuccess();
   }

   public void onStoryLiked(SuccessStory successStory) {
      view.updateStoryLike(successStory.isLiked());
      storyLikedEventDelegate.post(successStory);
   }

   public void share() {
      view.showShareDialog();
   }

   public void onShare(@ShareType String type, SuccessStory successStory) {
      view.openShare(successStory.getSharingUrl(), type);
      analyticsInteractor.analyticsActionPipe().send(new ShareSuccessStoryAction(type, successStory.getUrl()));
   }

   public interface View extends WebViewFragmentPresenter.View {

      void showShareDialog();

      void likeRequestSuccess();

      void openShare(String url, @ShareType String type);

      void updateStoryLike(boolean isLiked);
   }

}
