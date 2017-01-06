package com.worldventures.dreamtrips.modules.feed.view.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;

public final class TranslationDelegate<V extends RxView & TranslationDelegate.View> {

   private V view;
   private List<FeedItem> feedItems;

   private TranslationFeedInteractor translationFeedInteractor;

   public TranslationDelegate(TranslationFeedInteractor translationFeedInteractor) {
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

   public void translate(FeedEntity translatableItem, String languageTo) {
      translationFeedInteractor.translateFeedEntityPipe()
            .send(new TranslateUidItemCommand.TranslateFeedEntityCommand(translatableItem, languageTo));
   }

   private void subscribeToPostTranslation() {
      view.bindUntilDropView(translationFeedInteractor.translateFeedEntityPipe()
            .observe()
            .compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<TranslateUidItemCommand.TranslateFeedEntityCommand>()
                  .onSuccess(translatePostCommand -> translateSuccess(translatePostCommand.getResult()))
                  .onFail(this::translateFail));
   }

   private void translateSuccess(FeedEntity translatableItem) {
      int size = feedItems.size();
      for (int i = 0; i < size; i++) {
         FeedItem feedItem = feedItems.get(i);
         if (feedItem.getItem().equals(translatableItem)) {
            feedItem.setItem(translatableItem);
            view.updateItem(feedItem);
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
