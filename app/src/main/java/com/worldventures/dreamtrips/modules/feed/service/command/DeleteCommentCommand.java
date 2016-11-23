package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.comment.DeleteCommentHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class DeleteCommentCommand extends CommandWithError implements InjectableAction {

   private String commentId;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   public DeleteCommentCommand(String commentId) {
      this.commentId = commentId;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(DeleteCommentHttpAction.class)
            .createObservableResult(new DeleteCommentHttpAction(commentId))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_delete_comment;
   }
}