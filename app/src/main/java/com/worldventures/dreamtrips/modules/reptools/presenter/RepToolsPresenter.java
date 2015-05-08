package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;

public class RepToolsPresenter extends Presenter<RepToolsPresenter.View> {
    public RepToolsPresenter(View view) {
        super(view);
    }

    public boolean showInvite() {
        return getUser().isRep();
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);
    }

}
