package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

import java.util.List;

public class SettingsGroupPresenter extends Presenter<SettingsGroupPresenter.View> {

    public void handleCellClick(SettingsGroup model) {

    }

    public interface View extends Presenter.View {

        void setSettings(List<SettingsGroup> settings);

        void openSettings(Route route, SettingsGroup model);
    }
}
