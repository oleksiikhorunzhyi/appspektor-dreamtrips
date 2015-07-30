package com.worldventures.dreamtrips.modules.feed.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;
import com.worldventures.dreamtrips.modules.feed.presenter.CommentsActivityPresenter;

@Layout(R.layout.activity_comments)
public class CommentsActivity extends ToolbarActivity<CommentsActivityPresenter> {

    @Override
    protected int getToolbarTitle() {
        return R.string.comments_title;
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
    }

    @Override
    protected CommentsActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        return new CommentsActivityPresenter(bundleExtra);
    }
}