package com.worldventures.dreamtrips.modules.common.presenter;

public class ActivityPresenter<VT extends Presenter.View> extends Presenter<VT> {
    public ActivityPresenter(VT view) {
        super(view);
    }
}
