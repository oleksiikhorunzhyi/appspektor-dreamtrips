package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.core.utils.events.ServerDownEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateRegionsAndThemesEvent;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    public MainActivityPresenter(View view) {
        super(view);
    }

    public void restoreInstanceState() {
        view.setTitle(fragmentCompass.getCurrentState().getTitle());
    }

    public void onEvent(ServerDownEvent event) {
        view.alert(event.getMessage());
    }

    public void loadFilters() {
        eventBus.post(new UpdateRegionsAndThemesEvent());
    }

    public interface View extends Presenter.View {
        void setTitle(int title);
    }
}
