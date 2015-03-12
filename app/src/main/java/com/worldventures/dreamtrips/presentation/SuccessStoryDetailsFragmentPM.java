package com.worldventures.dreamtrips.presentation;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.model.SuccessStory;

import static com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest.LikeSS;
import static com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest.UnlikeSS;

public class SuccessStoryDetailsFragmentPM extends WebViewFragmentPresentation<SuccessStoryDetailsFragmentPM.View> {
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
            }
        };
        if (successStory.isLiked()) {
            dreamSpiceManager.execute(new UnlikeSS(successStory.getId()), callback);
        } else {
            dreamSpiceManager.execute(new LikeSS(successStory.getId()), callback);
        }
    }

    public void share() {
        view.showShareDialog();
    }


    public static interface View extends BasePresentation.View {
        void showShareDialog();

        void likeRequestSuccess();
    }


    public void onFbShare(SuccessStory successStory) {
        activityRouter.openShareFacebook(null, successStory.getSharingUrl());
    }

    public void onTwitterShare(SuccessStory successStory) {
        activityRouter.openShareTwitter(null, successStory.getSharingUrl());
    }

}
