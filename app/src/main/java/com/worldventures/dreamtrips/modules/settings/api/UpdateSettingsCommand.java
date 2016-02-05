package com.worldventures.dreamtrips.modules.settings.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.model.SettingsHolder;

import java.util.List;

public class UpdateSettingsCommand extends Command<Void> {

    private SettingsHolder settingsHolder;

    public UpdateSettingsCommand(List<Setting> settingsList) {
        super(Void.class);
        settingsHolder = new SettingsHolder(settingsList);
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().updateSettings(settingsHolder);
    }
}
