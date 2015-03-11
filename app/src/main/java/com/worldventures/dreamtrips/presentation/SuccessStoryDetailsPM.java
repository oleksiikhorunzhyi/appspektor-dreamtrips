package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.model.SuccessStory;

public class SuccessStoryDetailsPM extends WebViewFragmentPresentation<SuccessStoryDetailsPM.View> {
    public SuccessStoryDetailsPM(View view) {
        super(view);
    }

    public void like() {
        view.informUser("Will be implemented: like");
    }

    public void share() {
        view.showShareDialog();
    }


    public static interface View extends BasePresentation.View {
        void showShareDialog();
    }


    public void onFbShare(SuccessStory successStory) {
        activityRouter.openShareFacebook(successStory.getUrl(), successStory.getUrl());
    }

    public void onTwitterShare(SuccessStory successStory) {
        activityRouter.openShareTwitter(successStory.getUrl(), successStory.getUrl());
    }

}
