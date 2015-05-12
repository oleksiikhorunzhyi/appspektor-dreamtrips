package com.worldventures.dreamtrips.modules.membership.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplateActivityPresenter;

@Layout(R.layout.activity_invite)
public class EditTemplateActivity extends ToolbarActivity<EditTemplateActivityPresenter> {

    public static final String BUNDLE_TEMPLATE = "BUNDLE_TEMPLATE";

    @Override
    protected EditTemplateActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        InviteTemplate template = getIntent()
                .getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getParcelable(BUNDLE_TEMPLATE);
        return new EditTemplateActivityPresenter(this, template);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationModel().onCreate();
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.title_edit_template;
    }

}
