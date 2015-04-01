package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryLikedEvent;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.UnlikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SuccessStoryDetailsFragmentPresenter extends WebViewFragmentPresenter<SuccessStoryDetailsFragmentPresenter.View> {

    @Inject
    @Global
    protected EventBus eventBus;

    public SuccessStoryDetailsFragmentPresenter(View view) {
        super(view);
    }

    public void like(SuccessStory successStory) {
        RequestListener<JsonObject> callback = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {

                view.likeRequestSuccess();
                eventBus.post(new SuccessStoryLikedEvent());
            }
        };
        if (successStory.isLiked()) {
            dreamSpiceManager.execute(new UnlikeSuccessStoryCommand(successStory.getId()), callback);
        } else {
            dreamSpiceManager.execute(new LikeSuccessStoryCommand(successStory.getId()), callback);
        }
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
