package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.TranslatableItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

public abstract class TranslateUidItemCommand<T extends TranslatableItem> extends CommandWithError<T> implements InjectableAction {

   @Inject Janet janet;

   private T translatableItem;
   private String languageTo;

   public TranslateUidItemCommand(T translatableItem, String languageTo) {
      this.translatableItem = translatableItem;
      this.languageTo = languageTo;
   }

   @Override
   protected void run(CommandCallback<T> callback) throws Throwable {
      janet.createPipe(TranslateTextCachedCommand.class, Schedulers.io())
            .createObservableResult(new TranslateTextCachedCommand(translatableItem.getOriginalText(), languageTo))
            .map(TranslateTextCachedCommand::getResult)
            .map(this::mapResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private T mapResult(String translation) {
      translatableItem.setTranslation(translation);
      translatableItem.setTranslated(true);
      return translatableItem;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_translate_text;
   }

   @CommandAction
   public static class TranslateFeedEntityCommand extends TranslateUidItemCommand<FeedEntity> {
      public TranslateFeedEntityCommand(FeedEntity translatableItem, String languageTo) {
         super(translatableItem, languageTo);
      }
   }

   @CommandAction
   public static class TranslateCommentCommand extends TranslateUidItemCommand<Comment> {
      public TranslateCommentCommand(Comment translatableItem, String languageTo) {
         super(translatableItem, languageTo);
      }
   }

}
