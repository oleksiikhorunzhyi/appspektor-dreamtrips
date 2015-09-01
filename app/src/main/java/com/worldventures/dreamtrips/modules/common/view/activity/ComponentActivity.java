package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.activity_component)
public class ComponentActivity extends ToolbarActivity<ComponentPresenter> implements ComponentPresenter.View {

    Bundle extras;

    @Optional
    @InjectView(R.id.container_details_floating)
    protected FrameLayout detailsFloatingContainer;

    @Override
    protected int getToolbarTitle() {
        return getPresentationModel().getTitle();
    }

    @Override
    protected void beforeCreateView(Bundle savedInstanceState) {
        extras = getIntent().getBundleExtra(ComponentPresenter.COMPONENT_EXTRA);
        super.beforeCreateView(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {
        ToolbarConfig toolbarConfig = (ToolbarConfig)
                extras.getSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG);

        if (toolbarConfig != null) {
            toolbar.setVisibility(toolbarConfig.isVisible() ? View.VISIBLE : View.GONE);
            toolbar.setAlpha(toolbarConfig.getAlpha());
        }
    }

    boolean handleComponentChange() {
        if (detailsFloatingContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removeEdit();
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

    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    protected ComponentPresenter createPresentationModel(Bundle savedInstanceState) {
        return new ComponentPresenter(extras);
    }
}
