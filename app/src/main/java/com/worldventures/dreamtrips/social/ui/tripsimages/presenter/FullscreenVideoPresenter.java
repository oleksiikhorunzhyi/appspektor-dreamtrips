package com.worldventures.dreamtrips.social.ui.tripsimages.presenter;


import android.support.v4.app.FragmentManager;


import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.social.ui.flags.model.FlagData;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagDelegate;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FullscreenVideoPresenter extends Presenter<FullscreenVideoPresenter.View> implements FeedEntityHolder {

   @Inject Router router;
   @Inject FeedInteractor feedInteractor;
   @Inject FlagsInteractor flagsInteractor;
   @Inject FragmentManager fragmentManager;
   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;

   private Video video;
   private FlagDelegate flagDelegate;

   public FullscreenVideoPresenter(Video video) {
      this.video = video;
   }

   @Override
   public void onInjected() {
      super.onInjected();
      flagDelegate = new FlagDelegate(flagsInteractor);
   }

   @Override
   public void takeView(FullscreenVideoPresenter.View view) {
      super.takeView(view);
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
      view.setVideo(video);
      view.setSocialInfo(video, enableEdit(), enableDelete());
      loadEntity();
   }

   private void loadEntity() {
      feedInteractor.getFeedEntityPipe()
            .createObservable(new GetFeedEntityCommand(video.getUid(), com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type.VIDEO))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetFeedEntityCommand>()
                  .onSuccess(getFeedEntityCommand -> updateFeedEntity(getFeedEntityCommand.getResult()))
                  .onFail(this::handleError));
   }

   public void onLike() {
      feedInteractor.changeFeedEntityLikedStatusPipe().send(new ChangeFeedEntityLikedStatusCommand(video));
   }

   public void onComment() {
      new NavigationWrapperFactory().componentOrDialogNavigationWrapper(router, fragmentManager, view)
            .navigate(CommentableFragment.class, new CommentsBundle(video, false, true));
   }

   public void onDelete() {
      feedInteractor.deleteVideoPipe().send(new DeleteVideoCommand(video));
   }

   public void onFlag(Flaggable flaggable) {
      view.showFlagProgress();
      flagDelegate.loadFlags(flaggable, (command, throwable) -> {
         view.hideFlagProgress();
         handleError(command, throwable);
      });
   }

   public void sendFlagAction(int flagReasonId, String reason) {
      flagDelegate.flagItem(new FlagData(video.getUid(), flagReasonId, reason), view, this::handleError);
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      if (updatedFeedEntity.getUid().equals(video.getUid())) {
         video = (Video) updatedFeedEntity;
         view.setSocialInfo(video, enableEdit(), enableDelete());
      }
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
      // we delete entity in TripImagesViewPagerPresenter
   }

   private boolean enableEdit() {
      return !getAccount().equals(video.getOwner());
   }

   private boolean enableDelete() {
      return getAccount().equals(video.getOwner());
   }

   public void openUser() {
      view.openUser(new UserBundle(video.getOwner()));
   }

   public interface View extends Presenter.View, Flaggable, FlagDelegate.View {
      void setVideo(Video video);

      void openUser(UserBundle bundle);

      void setSocialInfo(Video video, boolean enableFlagging, boolean enableDelete);

      void showFlagProgress();

      void hideFlagProgress();
   }
}
