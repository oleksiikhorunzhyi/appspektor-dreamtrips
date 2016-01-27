package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.settings.api.GetSettingsQuery;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.settings.util.SettingsGroupFactory;

import java.util.List;

import javax.inject.Inject;

public class SettingsGroupPresenter extends Presenter<SettingsGroupPresenter.View> {

    @Inject
    SnappyRepository db;

    private boolean settingsLoaded;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        SettingsGroupFactory settingsGroupFactory = new SettingsGroupFactory(context);
        view.setSettings(settingsGroupFactory.createSettingsGroups());
    }

    public void loadSettings() {
        doRequest(new GetSettingsQuery(), settings -> {
            db.saveSettings(settings);
            settingsLoaded = true;
        });
    }

    public void handleCellClick(SettingsGroup model) {
        Route route;
        switch (model.getType()) {
            case GENERAL:
                route = Route.SETTINGS_GENERAL;
                break;
            case NOTIFICATIONS:
                route = Route.SETTINGS_NOTIFICATIONS;
                break;
            default:
                route = null;
                break;
        }
        //
        if (route != null && settingsLoaded) view.openSettings(route, model);
    }

    public interface View extends Presenter.View {

        void setSettings(List<SettingsGroup> settings);

        void openSettings(Route route, SettingsGroup model);
    }
}
