package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.TranslatePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.view.util.TextualPostTranslationDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.api.DownloadImageCommand;

import javax.inject.Inject;

import icepick.State;

public class FeedItemDetailsPresenter extends FeedDetailsPresenter<FeedItemDetailsPresenter.View> {

    @Inject TranslationFeedInteractor translationFeedInteractor;

    private FeedItem feedItem;
    private TextualPostTranslationDelegate textualPostTranslationDelegate;

    public FeedItemDetailsPresenter(FeedItem feedItem) {
        super(feedItem);
        this.feedItem = feedItem;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        textualPostTranslationDelegate.onTakeView(view, feedItem);
    }

    @Override
    public void dropView() {
        super.dropView();
        textualPostTranslationDelegate.onDropView();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        textualPostTranslationDelegate = new TextualPostTranslationDelegate(translationFeedInteractor);
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
            textualPostTranslationDelegate.translate(event.getTextualPost(), getAccount().getLocale());
        }
    }

    public interface View extends FeedDetailsPresenter.View, TextualPostTranslationDelegate.View  {

        void showAdditionalInfo(User user);
    }
}
