package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.hashtags.GetHashtagsSearchAction;
import com.worldventures.dreamtrips.api.hashtags.model.HashtagsSearchParams;
import com.worldventures.dreamtrips.api.hashtags.model.ImmutableHashtagsSearchParams;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.MetaData;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class FeedByHashtagCommand extends CommandWithError<List<FeedItem>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private String query;
   private int perPage;
   private Date before;

   public FeedByHashtagCommand(String query, int perPage, Date before) {
      this.query = query;
      this.perPage = perPage;
      this.before = before;
   }

   @Override
   protected void run(CommandCallback<List<FeedItem>> callback) throws Throwable {
      HashtagsSearchParams hashtagsSearchParams = ImmutableHashtagsSearchParams.builder()
            .query(query)
            .pageSize(perPage)
            .before(before)
            .type(HashtagsSearchParams.Type.POST)
            .build();
      janet.createPipe(GetHashtagsSearchAction.class, Schedulers.io())
            .createObservableResult(new GetHashtagsSearchAction(hashtagsSearchParams))
            .map(action -> {
               MetaData metaData = mapperyContext.convert(action.metadata(), MetaData.class);
               List<FeedItem> feedItems = mapperyContext.convert(action.response(), FeedItem.class);
               for (FeedItem feedItem : feedItems) {
                  feedItem.setMetaData(metaData);
               }
               return feedItems;
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_feeds_by_hashtag;
   }

   @CommandAction
   public static class LoadNext extends FeedByHashtagCommand {
      public LoadNext(String query, int perPage, Date before) {
         super(query, perPage, before);
      }
   }

   @CommandAction
   public static class Refresh extends FeedByHashtagCommand {
      public Refresh(String query, int perPage) {
         super(query, perPage, null);
      }
   }
}
