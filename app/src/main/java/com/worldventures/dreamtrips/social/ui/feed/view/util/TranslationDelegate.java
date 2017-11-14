package com.worldventures.dreamtrips.social.ui.feed.view.util;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.TranslateUidItemCommand;

import java.util.Collections;
import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class TranslationDelegate {

   private TranslationDelegate.View view;
   private List<FeedItem> feedItems;

   private final TranslationFeedInteractor translationFeedInteractor;
   private Observable.Transformer stopper;

   public TranslationDelegate(TranslationFeedInteractor translationFeedInteractor) {
      this.translationFeedInteractor = translationFeedInteractor;
   }

   public void onTakeView(TranslationDelegate.View view, List<FeedItem> feedItems, Observable.Transformer stopper) {
      this.view = view;
      this.feedItems = feedItems;
      this.stopper = stopper;
      subscribeToPostTranslation();
   }

   public void onTakeView(TranslationDelegate.View view, FeedItem feedItem, Observable.Transformer stopper) {
      onTakeView(view, Collections.singletonList(feedItem), stopper);
   }

   public void onDropView() {
      view = null;
   }

   public void translate(FeedEntity translatableItem) {
      translationFeedInteractor.translateFeedEntityPipe()
            .send(new TranslateUidItemCommand.TranslateFeedEntityCommand(translatableItem, LocaleHelper.getDefaultLocaleFormatted()));
   }

   public void showOriginal(FeedEntity translatableItem) {
      int size = feedItems.size();
      for (int i = 0; i < size; i++) {
         FeedItem feedItem = feedItems.get(i);
         if (feedItem.getItem().equals(translatableItem)) {
            translatableItem.setTranslated(false);
            feedItem.setItem(translatableItem);
            view.updateItem(feedItem);
            return;
         }
      }
   }

   private void subscribeToPostTranslation() {
      translationFeedInteractor.translateFeedEntityPipe()
            .observe()
            .compose(new IoToMainComposer<>())
            .compose(stopper)
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

      void informUser(String errorMessage);
   }
}
