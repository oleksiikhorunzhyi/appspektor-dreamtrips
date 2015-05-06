package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class RepToolsPresenter extends Presenter {
    public RepToolsPresenter(View view) {
        super(view);
    }

    public boolean showInvite() {
        return getUser().isRep();
    }
}
