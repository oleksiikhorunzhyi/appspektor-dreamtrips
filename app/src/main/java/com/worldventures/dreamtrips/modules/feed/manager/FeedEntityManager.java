package com.worldventures.dreamtrips.modules.feed.manager;

import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FeedEntityManagerListener;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.UnlikeEntityCommand;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class FeedEntityManager {

   private FeedEntityManagerListener feedEntityManagerListener;
   private List<String> uidsWithPendingLikes = new ArrayList<>();

   private LikesInteractor likesInteractor;

   public FeedEntityManager(LikesInteractor likesInteractor) {
      this.likesInteractor = likesInteractor;
   }

   public void setFeedEntityManagerListener(FeedEntityManagerListener feedEntityManagerListener) {
      this.feedEntityManagerListener = feedEntityManagerListener;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Likes
   ///////////////////////////////////////////////////////////////////////////

   public void like(FeedEntity feedEntity) {
      if (uidsWithPendingLikes.contains(feedEntity.getUid())) {
         return;
      }
      uidsWithPendingLikes.add(feedEntity.getUid());
      likesInteractor.likePipe()
            .createObservable(new LikeEntityCommand(feedEntity.getUid()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<LikeEntityCommand>()
                  .onSuccess(likeEntityCommand -> {
                     actualizeLikes(feedEntity, true);
                     //eventBus.post(new EntityLikedEvent(feedEntity));
                     uidsWithPendingLikes.remove(feedEntity.getUid());
                  })
                  .onFail((command, throwable) -> handleLikeCommandError(feedEntity, command, throwable))
            );
   }

   public void unlike(FeedEntity feedEntity) {
      if (uidsWithPendingLikes.contains(feedEntity.getUid())) {
         return;
      }
      uidsWithPendingLikes.add(feedEntity.getUid());

      likesInteractor.unlikePipe()
            .createObservable(new UnlikeEntityCommand(feedEntity.getUid()))
            .compose(new IoToMainComposer<>())
            .subscribe(new ActionStateSubscriber<UnlikeEntityCommand>()
                  .onSuccess(command -> {
                     actualizeLikes(feedEntity, false);
                    // eventBus.post(new EntityLikedEvent(feedEntity));
                     uidsWithPendingLikes.remove(feedEntity.getUid());
                  })
                  .onFail((command, throwable) -> handleLikeCommandError(feedEntity, command, throwable))
            );
   }

   private void actualizeLikes(FeedEntity feedEntity, boolean liked) {
      feedEntity.setLiked(liked);
      int currentCount = feedEntity.getLikesCount();
      currentCount = feedEntity.isLiked() ? currentCount + 1 : currentCount - 1;
      feedEntity.setLikesCount(currentCount);
   }

   private void handleLikeCommandError(FeedEntity feedEntity, Command command, Throwable throwable) {
      feedEntityManagerListener.handleError(command, throwable);
      Timber.e(throwable, this.getClass().getSimpleName());
      uidsWithPendingLikes.remove(feedEntity.getUid());
   }
}
