package com.worldventures.dreamtrips.modules.feed.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.feed.GetFeedHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public abstract class BaseGetFeedCommand<HttpAction extends GetFeedHttpAction> extends CommandWithError<List<FeedItem>> implements InjectableAction {

   protected static final int FEED_LIMIT = 20;
   protected static final int TIMELINE_LIMIT = 10;

   @Inject protected Janet janet;
   @Inject protected MapperyContext mappery;
   @Inject protected SessionHolder<UserSession> userSessionHolder;

   protected Date before;

   public BaseGetFeedCommand() {
   }

   public BaseGetFeedCommand(Date before) {
      this.before = before;
   }

   @Override
   protected void run(CommandCallback<List<FeedItem>> callback) throws Throwable {
      janet.createPipe(provideHttpActionClass())
            .createObservableResult(provideRequest())
            .map(action -> mappery.convert(action.response(), FeedItem.class))
            .doOnNext(items -> items.add(0, createVideoItem()))
            .subscribe(feedItems -> itemsLoaded(callback, feedItems), callback::onFail);
   }

   private FeedItem createVideoItem() {
      Video video = new Video();
      video.setUid(UUID.randomUUID().toString());
      video.setOwner(userSessionHolder.get().get().getUser());
      video.setThumbnail("https://p.widencdn.net/u4mqx6/4k31s.m4v");
      video.setHdUrl("https://p.widencdn.net/stream/hd/sandbox/b2xvfyfbyf/b2xvfyfbyf.mp4");
      video.setSdUrl("https://p.widencdn.net/stream/sd/sandbox/b2xvfyfbyf/b2xvfyfbyf.mp4");
      video.setAspectRatio(1.77d);
      video.setDuration(31000L);

      TextualPost textualPost = new TextualPost();
      textualPost.setUid(UUID.randomUUID().toString());
      textualPost.setDescription("Video demo post");
      textualPost.setOwner(userSessionHolder.get().get().getUser());
      textualPost.setAttachments(Collections.singletonList(FeedItem.create(video, userSessionHolder.get()
            .get()
            .getUser())));
      return FeedItem.create(textualPost, userSessionHolder.get().get().getUser());
   }

   protected void itemsLoaded(CommandCallback<List<FeedItem>> callback, List<FeedItem> items) {
      callback.onSuccess(items);
   }

   protected abstract HttpAction provideRequest();

   protected abstract Class<HttpAction> provideHttpActionClass();
}
