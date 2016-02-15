package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.tripsimages.api.DownloadImageCommand;

public class FeedItemDetailsPresenter extends FeedDetailsPresenter<FeedItemDetailsPresenter.View> {


    public FeedItemDetailsPresenter(FeedItem feedItem) {
        super(feedItem);
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

    public interface View extends FeedDetailsPresenter.View {

        void showAdditionalInfo(User user);
    }
}
