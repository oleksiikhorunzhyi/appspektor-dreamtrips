package com.worldventures.dreamtrips.modules.feed.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;

@Layout(R.layout.activity_comments)
public class CommentsActivity extends ToolbarActivity<ActivityPresenter> {

    @Override
    protected int getToolbarTitle() {
        return R.string.comments_title;
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
    }

    @Override
    protected ActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new ActivityPresenter();
    }
}