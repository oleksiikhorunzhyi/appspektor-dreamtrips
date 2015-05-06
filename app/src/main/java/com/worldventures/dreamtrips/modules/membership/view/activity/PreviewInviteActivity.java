package com.worldventures.dreamtrips.modules.membership.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.PreviewInvitePresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_invite)
public class PreviewInviteActivity extends ActivityWithPresenter<Presenter> {

    public static final String BUNDLE_TEMPLATE = "BUNDLE_TEMPLATE";
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Override
    protected Presenter createPresentationModel(Bundle savedInstanceState) {
        InviteTemplate template = getIntent()
                .getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getParcelable(BUNDLE_TEMPLATE);
        return new PreviewInvitePresenter(this, template);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_edit_template);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}
