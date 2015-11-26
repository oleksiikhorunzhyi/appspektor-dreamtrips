package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.UnlikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

public class SuccessStoryDetailsPresenter extends WebViewFragmentPresenter<SuccessStoryDetailsPresenter.View> {

    private SuccessStory successStory;

    public SuccessStoryDetailsPresenter(SuccessStory story, String url) {
        super(url);
        this.successStory = story;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.viewSS(getAccountUserId(), successStory.getId());
    }

    public void like(SuccessStory successStory) {
        if (successStory.isLiked()) {
            TrackingHelper.unlikeSS(getAccountUserId(), successStory.getId());
            doRequest(new UnlikeSuccessStoryCommand(successStory.getId()),
                    (object) -> onLiked());
        } else {
            TrackingHelper.likeSS(getAccountUserId(), successStory.getId());
            doRequest(new LikeSuccessStoryCommand(successStory.getId()),
                    (object) -> onLiked());
        }
    }

    private void onLiked() {
        view.likeRequestSuccess();
    }

    public void share() {
        view.showShareDialog();
    }

    public void onShare(@ShareFragment.ShareType String type, SuccessStory successStory) {
        view.openShare(successStory.getSharingUrl(), type);
    }

    public interface View extends WebViewFragmentPresenter.View {
        void showShareDialog();

        void likeRequestSuccess();

        void openShare(String url, @ShareFragment.ShareType String type);
    }

}
