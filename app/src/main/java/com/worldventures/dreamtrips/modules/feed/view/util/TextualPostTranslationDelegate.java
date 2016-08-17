package com.worldventures.dreamtrips.modules.feed.view.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;

public final class TextualPostTranslationDelegate<V extends RxView & TextualPostTranslationDelegate.View> {

   private V view;
   private List<FeedItem> feedItems;

   private TranslationFeedInteractor translationFeedInteractor;

   public TextualPostTranslationDelegate(TranslationFeedInteractor translationFeedInteractor) {
      this.translationFeedInteractor = translationFeedInteractor;
   }

   public void onTakeView(V view, List<FeedItem> feedItems) {
      this.view = view;
      this.feedItems = feedItems;
      subscribeToPostTranslation();
   }

   public void onTakeView(V view, FeedItem feedItem) {
      onTakeView(view, Queryable.from(feedItem).toList());
   }

   public void onDropView() {
      view = null;
   }

   public void translate(PostFeedItem postFeedItem, String languageTo) {
      translationFeedInteractor.translatePostPipe().send(TranslateUidItemCommand.forPost(postFeedItem, languageTo));
   }

   private void subscribeToPostTranslation() {
      view.bindUntilDropView(translationFeedInteractor.translatePostPipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<TranslateUidItemCommand.TranslatePostCommand>().onSuccess(translatePostCommand -> translateSuccess(translatePostCommand
                  .getResult())).onFail(this::translateFail));
   }

   private void translateSuccess(FeedItem postFeedItem) {
      int size = feedItems.size();
      for (int i = 0; i < size; i++) {
         if (feedItems.get(i).equalsWith(postFeedItem)) {
            feedItems.set(i, postFeedItem);
            view.updateItem(postFeedItem);
            return;
         }
      }
      view.updateItem(null);
   }

   private void translateFail(CommandWithError action, Throwable throwable) {
      view.informUser(action.getErrorMessage());
      view.updateItem(null);
   }

   public interface View {
      void updateItem(FeedItem feedItem);
   }
}
