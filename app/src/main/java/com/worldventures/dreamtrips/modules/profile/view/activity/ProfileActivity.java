package com.worldventures.dreamtrips.modules.profile.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfileActivityPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_profile)
public class ProfileActivity extends ActivityWithPresenter<ProfileActivityPresenter> {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @InjectView(R.id.container_details_floating)
    View detailsFloatingContainer;

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected ProfileActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new ProfileActivityPresenter(getIntent().getExtras());
    }

    @Override
    public void onBackPressed() {
        if (!handleComponentChange()) super.onBackPressed();
    }

    boolean handleComponentChange() {
        if (detailsFloatingContainer != null && detailsFloatingContainer.getVisibility() == View.VISIBLE) {
            fragmentCompass.removePost();
            detailsFloatingContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

}
