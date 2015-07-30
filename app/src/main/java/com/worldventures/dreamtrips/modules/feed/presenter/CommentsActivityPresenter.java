package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class CommentsActivityPresenter extends Presenter {

    private Bundle bundle;

    public CommentsActivityPresenter(Bundle bundle) {
        super();
        this.bundle = bundle;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        openComments();
    }

    public void openComments() {
        Bundle bundle = new Bundle();
        fragmentCompass.switchBranch(Route.PHOTO_COMMENTS, bundle);
    }

}
