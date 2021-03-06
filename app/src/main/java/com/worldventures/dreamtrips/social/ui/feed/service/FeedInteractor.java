package com.worldventures.dreamtrips.social.ui.feed.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetPhotoCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetVideoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class FeedInteractor {

   private final ActionPipe<GetAccountFeedCommand.Refresh> refreshAccountFeedPipe;
   private final ActionPipe<GetAccountFeedCommand.LoadNext> loadNextAccountFeedPipe;
   private final ActionPipe<GetUserTimelineCommand.Refresh> refreshUserTimelinePipe;
   private final ActionPipe<GetUserTimelineCommand.LoadNext> loadNextUserTimelinePipe;
   private final ActionPipe<GetAccountTimelineCommand.Refresh> refreshAccountTimelinePipe;
   private final ActionPipe<GetAccountTimelineCommand.LoadNext> loadNextAccountTimelinePipe;
   private final ActionPipe<GetFeedEntityCommand> getFeedEntityPipe;
   private final ActionPipe<GetPhotoCommand> getPhotoCommandPipe;
   private final ActionPipe<GetVideoCommand> getVideoCommandPipe;
   private final ActionPipe<DeleteVideoCommand> deleteVideoPipe;
   private final ActionPipe<ChangeFeedEntityLikedStatusCommand> changeFeedEntityLikedStatusPipe;

   @Inject
   public FeedInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      refreshAccountFeedPipe = sessionActionPipeCreator.createPipe(GetAccountFeedCommand.Refresh.class,
            Schedulers.io());
      loadNextAccountFeedPipe = sessionActionPipeCreator.createPipe(GetAccountFeedCommand.LoadNext.class,
            Schedulers.io());
      refreshUserTimelinePipe = sessionActionPipeCreator.createPipe(GetUserTimelineCommand.Refresh.class,
            Schedulers.io());
      loadNextUserTimelinePipe = sessionActionPipeCreator.createPipe(GetUserTimelineCommand.LoadNext.class,
            Schedulers.io());
      refreshAccountTimelinePipe = sessionActionPipeCreator.createPipe(GetAccountTimelineCommand.Refresh.class,
            Schedulers.io());
      loadNextAccountTimelinePipe = sessionActionPipeCreator.createPipe(GetAccountTimelineCommand.LoadNext.class,
            Schedulers.io());
      getFeedEntityPipe = sessionActionPipeCreator.createPipe(GetFeedEntityCommand.class, Schedulers.io());
      getPhotoCommandPipe = sessionActionPipeCreator.createPipe(GetPhotoCommand.class, Schedulers.io());
      getVideoCommandPipe = sessionActionPipeCreator.createPipe(GetVideoCommand.class, Schedulers.io());
      changeFeedEntityLikedStatusPipe = sessionActionPipeCreator.createPipe(ChangeFeedEntityLikedStatusCommand.class,
            Schedulers.io());
      deleteVideoPipe = sessionActionPipeCreator.createPipe(DeleteVideoCommand.class, Schedulers.io());
   }

   public ActionPipe<GetAccountFeedCommand.Refresh> getRefreshAccountFeedPipe() {
      return refreshAccountFeedPipe;
   }

   public ActionPipe<GetAccountFeedCommand.LoadNext> getLoadNextAccountFeedPipe() {
      return loadNextAccountFeedPipe;
   }

   public ActionPipe<GetUserTimelineCommand.Refresh> getRefreshUserTimelinePipe() {
      return refreshUserTimelinePipe;
   }

   public ActionPipe<GetUserTimelineCommand.LoadNext> getLoadNextUserTimelinePipe() {
      return loadNextUserTimelinePipe;
   }

   public ActionPipe<GetAccountTimelineCommand.Refresh> getRefreshAccountTimelinePipe() {
      return refreshAccountTimelinePipe;
   }

   public ActionPipe<GetAccountTimelineCommand.LoadNext> getLoadNextAccountTimelinePipe() {
      return loadNextAccountTimelinePipe;
   }

   public ActionPipe<GetFeedEntityCommand> getFeedEntityPipe() {
      return getFeedEntityPipe;
   }

   public ActionPipe<GetPhotoCommand> getPhotoCommandPipe() {
      return getPhotoCommandPipe;
   }

   public ActionPipe<GetVideoCommand> getVideoCommandPipe() {
      return getVideoCommandPipe;
   }

   public ActionPipe<DeleteVideoCommand> deleteVideoPipe() {
      return deleteVideoPipe;
   }

   public ActionPipe<ChangeFeedEntityLikedStatusCommand> changeFeedEntityLikedStatusPipe() {
      return changeFeedEntityLikedStatusPipe;
   }
}
