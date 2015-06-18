package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.activity_component)
public class ComponentActivity extends ToolbarActivity<ComponentPresenter> {

    @InjectView(R.id.container_details)
    protected FrameLayout detailsContainer;
    @Optional
    @InjectView(R.id.container_details_floating)
    protected FrameLayout detailsFloatingContainer;
    @InjectView(R.id.container_details_fullscreen)
    protected FrameLayout detailsFullScreenContainer;

    @Override
    protected int getToolbarTitle() {
        return getPresentationModel().getTitle();
    }

    boolean handleComponentChange() {
        if (detailsFullScreenContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeEdit();
            detailsFullScreenContainer.setVisibility(View.GONE);
            return true;
        } else if (detailsContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeDetailed();
            detailsContainer.setVisibility(View.GONE);
            return true;
        } else if (detailsFloatingContainer != null && detailsFloatingContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeDetailed();
            detailsFloatingContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!handleComponentChange())
            super.onBackPressed();
    }

    @Override
    protected ComponentPresenter createPresentationModel(Bundle savedInstanceState) {
        return new ComponentPresenter(getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE));
    }
}
