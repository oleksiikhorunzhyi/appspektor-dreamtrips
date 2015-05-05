package com.worldventures.dreamtrips.modules.membership.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_invite)
public class SelectTemplateActivity extends ActivityWithPresenter<Presenter> {

    public static final String BUNDLE_TEMPLATE = "BUNDLE_TEMPLATE";
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Override
    protected Presenter createPresentationModel(Bundle savedInstanceState) {
        return new Presenter(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_template);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fragmentCompass.replace(Route.SELECT_INVITE_TEMPLATE);
    }
}

