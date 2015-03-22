package com.worldventures.dreamtrips.modules.common.presenter;


public class LaunchActivityPresenter extends Presenter<Presenter.View> {

    public LaunchActivityPresenter(View view) {
        super(view);
    }

    public boolean isLogged() {
        return appSessionHolder.get().isPresent();
    }
}
