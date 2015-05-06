package com.worldventures.dreamtrips.modules.membership.presenter;

import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class MembershipPresenter extends Presenter {
    public MembershipPresenter(View view) {
        super(view);
    }

    public boolean showInvite() {
        return !getUser().isRep();
    }
}
