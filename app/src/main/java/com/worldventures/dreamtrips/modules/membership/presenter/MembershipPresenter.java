package com.worldventures.dreamtrips.modules.membership.presenter;

import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;

public class MembershipPresenter extends Presenter<MembershipPresenter.View> {
    public MembershipPresenter(View view) {
        super(view);
    }

    public boolean showInvite() {
        return !getUser().isRep();
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);
    }
}
