package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.GetCommentsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class GetCommentsCommand extends CommandWithError<List<Comment>> implements InjectableAction {

   public static final int LIMIT = 10;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private final String itemUid;
   private final int page;

   public GetCommentsCommand(String itemUid, int page) {
      this.itemUid = itemUid;
      this.page = page;
   }

   @Override
   protected void run(CommandCallback<List<Comment>> callback) throws Throwable {
      janet.createPipe(GetCommentsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetCommentsHttpAction(itemUid, page, LIMIT))
            .map(action -> mapperyContext.convert(action.response(), Comment.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_comments;
   }
}
