package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.api.DownloadImageCommand;

import javax.inject.Inject;

public class FeedItemDetailsPresenter extends FeedDetailsPresenter<FeedItemDetailsPresenter.View> {

    @Inject TextualPostTranslationDelegate textualPostTranslationDelegate;

    public FeedItemDetailsPresenter(FeedItem feedItem) {
        super(feedItem);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        textualPostTranslationDelegate.onTakeView(view, feedItem, apiErrorPresenter);
    }

    @Override
    public void dropView() {
        apiErrorPresenter.dropView();
        textualPostTranslationDelegate.onDropView();
        super.dropView();
    }

    @Override
    protected void updateFullEventInfo(FeedEntityHolder feedEntityHolder) {
        super.updateFullEventInfo(feedEntityHolder);
        //
        if (view.isTabletLandscape())
            view.showAdditionalInfo(feedEntityHolder.getItem().getOwner());
    }

    public void onEvent(DownloadPhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DownloadImageCommand(context, event.url));
    }

    public void onEvent(TranslatePostEvent event) {
        if (view.isVisibleOnScreen()) {
            textualPostTranslationDelegate.translate(event.getTextualPost(), localeHelper.getOwnAccountLocaleFormatted());
        }
    }

    public interface View extends FeedDetailsPresenter.View, TextualPostTranslationDelegate.View  {

        void showAdditionalInfo(User user);
    }
}
