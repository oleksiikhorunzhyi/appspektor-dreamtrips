package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.GetCommentsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class GetCommentsCommand extends CommandWithError<List<Comment>> implements InjectableAction {

   public static final int LIMIT = 10;

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private final FeedEntity feedEntity;
   private final int page;

   public GetCommentsCommand(FeedEntity feedEntity, int page) {
      this.feedEntity = feedEntity;
      this.page = page;
   }

   @Override
   protected void run(CommandCallback<List<Comment>> callback) throws Throwable {
      janet.createPipe(GetCommentsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetCommentsHttpAction(feedEntity.getUid(), page, LIMIT))
            .map(action -> mapperyContext.convert(action.response(), Comment.class))
            .doOnNext(loadedComments -> {
               if (page == 1) {
                  feedEntity.setComments(loadedComments);
               } else {
                  List<Comment> comments = new ArrayList<>();
                  comments.addAll(loadedComments);
                  comments.addAll(feedEntity.getComments());
                  feedEntity.setComments(comments);
               }
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_comments;
   }
}
