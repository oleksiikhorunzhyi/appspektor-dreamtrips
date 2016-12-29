package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;

import javax.inject.Inject;

public class FeedItemDetailsPresenter extends FeedDetailsPresenter<FeedItemDetailsPresenter.View> {

   @Inject TextualPostTranslationDelegate textualPostTranslationDelegate;

   public FeedItemDetailsPresenter(FeedItem feedItem) {
      super(feedItem);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      textualPostTranslationDelegate.onTakeView(view, feedItem);
   }

   @Override
   public void dropView() {
      textualPostTranslationDelegate.onDropView();
      super.dropView();
   }

   @Override
   public void onDownloadImage(String url) {
      feedActionHandlerDelegate.onDownloadImage(url, bindViewToMainComposer(), this::handleError);
   }

   public void onEvent(TranslatePostEvent event) {
      if (view.isVisibleOnScreen()) {
         textualPostTranslationDelegate.translate(event.getPostFeedItem(), LocaleHelper.getDefaultLocaleFormatted());
      }
   }

   public interface View extends FeedDetailsPresenter.View, TextualPostTranslationDelegate.View {

   }
}
