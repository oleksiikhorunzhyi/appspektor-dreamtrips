package com.worldventures.dreamtrips.modules.reptools.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.PresentationModelDrivenActivity;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsActivityPM;

import butterknife.InjectView;

@Layout(R.layout.activity_success_story_details)
public class SuccessStoryDetailsActivity extends PresentationModelDrivenActivity<SuccessStoryDetailsActivityPM> implements BasePresenter.View {

    public static final String BUNDLE_STORY = "BUNDLE_STORY";
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;


    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        getPresentationModel().onCreate(getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getParcelable(BUNDLE_STORY));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected SuccessStoryDetailsActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new SuccessStoryDetailsActivityPM(this);
    }
}
