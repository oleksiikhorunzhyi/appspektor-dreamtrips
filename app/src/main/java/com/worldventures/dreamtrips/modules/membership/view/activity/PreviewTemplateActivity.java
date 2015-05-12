package com.worldventures.dreamtrips.modules.membership.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.PreviewTemplateActivityPresenter;

@Layout(R.layout.activity_invite)
public class PreviewTemplateActivity extends ToolbarActivity<PreviewTemplateActivityPresenter> {

    @Override
    protected int getToolbarTitle() {
        return R.string.preview_template;
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        getPresentationModel().showPreview();
    }

    @Override
    protected PreviewTemplateActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        return new PreviewTemplateActivityPresenter(this,
                bundleExtra.getString(StaticInfoFragment.BundleUrlFragment.URL_EXTRA));
    }
}