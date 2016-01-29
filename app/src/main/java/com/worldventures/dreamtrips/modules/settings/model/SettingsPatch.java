package com.worldventures.dreamtrips.modules.settings.model;

import java.util.List;

public class SettingsPatch {

    private List<Setting> settings;

    public SettingsPatch(List<Setting> settings) {
        this.settings = settings;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }
}
