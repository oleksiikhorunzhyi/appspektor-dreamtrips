package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.util.TranslationDelegate;

import javax.inject.Inject;

public class FeedItemDetailsPresenter extends FeedDetailsPresenter<FeedItemDetailsPresenter.View> {

   @Inject TranslationDelegate translationDelegate;

   public FeedItemDetailsPresenter(FeedItem feedItem) {
      super(feedItem);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      translationDelegate.onTakeView(view, feedItem, bindView());
   }

   @Override
   public void dropView() {
      translationDelegate.onDropView();
      super.dropView();
   }

   @Override
   public void onDownloadImage(String url) {
      feedActionHandlerDelegate.onDownloadImage(url, bindViewToMainComposer(), this::handleError);
   }

   @Override
   public void onTranslateFeedEntity(FeedEntity translatableItem) {
      translationDelegate.translate(translatableItem);
   }

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {
      translationDelegate.showOriginal(translatableItem);
   }

   public interface View extends FeedDetailsPresenter.View, TranslationDelegate.View {

   }
}
