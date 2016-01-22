package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.settings.model.Settings;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import java.util.List;

public class SettingsPresenter extends Presenter<SettingsPresenter.View> {

    private List<Settings> settingsList;

    public SettingsPresenter(SettingsGroup group, List<Settings> settingsList) {
        SettingsManager settingsManager = new SettingsManager();
        SettingsFactory settingsFactory = new SettingsFactory();
        this.settingsList = settingsManager.merge(settingsList, settingsFactory.createSettings(group));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setSettings(settingsList);
    }

    public interface View extends Presenter.View {

        void setSettings(List<Settings> settingsList);
    }
}
