package com.worldventures.dreamtrips.modules.membership.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;
import com.worldventures.dreamtrips.modules.membership.presenter.InviteTemplateSelectorPresenter;

@Layout(R.layout.activity_invite)
public class InviteTemplateSelectorActivity extends ToolbarActivity<InviteTemplateSelectorPresenter> {

    @Override
    protected InviteTemplateSelectorPresenter createPresentationModel(Bundle savedInstanceState) {
        return new InviteTemplateSelectorPresenter();
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.invitation_select_template;
    }
}

