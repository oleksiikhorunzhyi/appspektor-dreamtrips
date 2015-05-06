package com.worldventures.dreamtrips.modules.membership.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.InviteTemplateSelectorPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_invite)
public class InviteTemplateSelectorActivity extends ActivityWithPresenter<InviteTemplateSelectorPresenter> {

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Override
    protected InviteTemplateSelectorPresenter createPresentationModel(Bundle savedInstanceState) {
        return new InviteTemplateSelectorPresenter(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_template);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}

