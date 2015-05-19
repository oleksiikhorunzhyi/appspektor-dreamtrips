package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.utils.events.SuccessStoryLikedEvent;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.UnlikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

public class SuccessStoryDetailsFragmentPresenter extends WebViewFragmentPresenter<SuccessStoryDetailsFragmentPresenter.View> {

    public SuccessStoryDetailsFragmentPresenter(String url) {
        super(url);
    }

    public void like(SuccessStory successStory) {
        if (successStory.isLiked()) {
            doRequest(new UnlikeSuccessStoryCommand(successStory.getId()),
                    (object) -> onLiked());
        } else {
            doRequest(new LikeSuccessStoryCommand(successStory.getId()),
                    (object) -> onLiked());
        }
    }

    private void onLiked() {
        view.likeRequestSuccess();
        eventBus.post(new SuccessStoryLikedEvent());
    }

    public void share() {
        view.showShareDialog();
    }

    public void onFbShare(SuccessStory successStory) {
        activityRouter.openShareFacebook(null, successStory.getSharingUrl(), null);
    }

    public void onTwitterShare(SuccessStory successStory) {
        activityRouter.openShareTwitter(null, successStory.getSharingUrl(), null);
    }

    public void fullscreenEvent(SuccessStory story) {
        activityRouter.openSuccessStoryDetails(story);
    }

    public static interface View extends WebViewFragmentPresenter.View {
        void showShareDialog();

        void likeRequestSuccess();
    }

}
