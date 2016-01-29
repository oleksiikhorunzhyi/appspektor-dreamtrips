package com.worldventures.dreamtrips.modules.settings.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.model.SettingsPatch;

import java.util.List;

public class UpdateSettingsCommand extends Command<Void> {

    private SettingsPatch settingsPatch;

    public UpdateSettingsCommand(List<Setting> settingsList) {
        super(Void.class);
        settingsPatch = new SettingsPatch(settingsList);
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        return getService().updateSettings(settingsPatch);
    }
}
