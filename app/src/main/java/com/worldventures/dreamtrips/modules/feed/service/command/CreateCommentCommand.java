package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.CreateCommentHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class CreateCommentCommand extends CommandWithError<Comment> implements InjectableAction {

   private String objectId;
   private String text;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   public CreateCommentCommand(String objectId, String text) {
      this.objectId = objectId;
      this.text = text;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(CreateCommentHttpAction.class)
            .createObservableResult(new CreateCommentHttpAction(objectId, text))
            .map(CreateCommentHttpAction::response)
            .map(comment -> mappery.convert(comment, Comment.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_post_comment;
   }
}
