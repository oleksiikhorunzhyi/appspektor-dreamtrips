package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.LikeSuccessStory;
import com.worldventures.dreamtrips.modules.reptools.api.successstories.UnlikeSuccessStory;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.core.utils.events.SuccessStoryLikedEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SuccessStoryDetailsFragmentPM extends WebViewFragmentPresenter<SuccessStoryDetailsFragmentPM.View> {

    @Inject
    @Global
    EventBus eventBus;

    public SuccessStoryDetailsFragmentPM(View view) {
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
            dreamSpiceManager.execute(new UnlikeSuccessStory(successStory.getId()), callback);
        } else {
            dreamSpiceManager.execute(new LikeSuccessStory(successStory.getId()), callback);
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

    public static interface View extends BasePresenter.View {
        void showShareDialog();

        void likeRequestSuccess();
    }

}
