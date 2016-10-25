package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.UpdateCommentHttpAction;
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
public class EditCommentCommand extends CommandWithError<Comment> implements InjectableAction {

   private String commentId;
   private String text;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   public EditCommentCommand(String commentId, String text) {
      this.commentId = commentId;
      this.text = text;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(UpdateCommentHttpAction.class)
            .createObservableResult(new UpdateCommentHttpAction(commentId, text))
            .map(UpdateCommentHttpAction::response)
            .map(comment -> mappery.convert(comment, Comment.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_edit_comment;
   }
}
