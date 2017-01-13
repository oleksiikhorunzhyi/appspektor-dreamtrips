package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate;

import javax.inject.Inject;

public class FeedItemDetailsPresenter extends FeedDetailsPresenter<FeedItemDetailsPresenter.View> {

   @Inject TranslationDelegate translationDelegate;

   public FeedItemDetailsPresenter(FeedItem feedItem) {
      super(feedItem);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      translationDelegate.onTakeView(view, feedItem);
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
      translationDelegate.translate(translatableItem, LocaleHelper.getDefaultLocaleFormatted());
   }

   @Override
   public void onShowOriginal(FeedEntity translatableItem) {
      translationDelegate.showOriginal(translatableItem);
   }

   public interface View extends FeedDetailsPresenter.View, TranslationDelegate.View {

   }
}
