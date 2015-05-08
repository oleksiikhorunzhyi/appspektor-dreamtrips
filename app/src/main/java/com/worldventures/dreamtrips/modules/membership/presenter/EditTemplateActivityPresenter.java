package com.worldventures.dreamtrips.modules.membership.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;

public class EditTemplateActivityPresenter extends Presenter {

    private InviteTemplate template;

    public EditTemplateActivityPresenter(View view, InviteTemplate inviteTemplate) {
        super(view);
        this.template = inviteTemplate;
    }

    @Override
    public void onStart() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EditTemplateFragment.TEMPLATE, template);
        fragmentCompass.replace(Route.EDIT_INVITE_TEMPLATE, bundle);
    }
}
