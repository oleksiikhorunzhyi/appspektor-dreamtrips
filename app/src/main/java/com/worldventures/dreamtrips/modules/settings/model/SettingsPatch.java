package com.worldventures.dreamtrips.modules.settings.model;

import java.util.List;

public class SettingsPatch {

    private List<Settings> settings;

    public SettingsPatch(List<Settings> settings) {
        this.settings = settings;
    }

    public List<Settings> getSettings() {
        return settings;
    }

    public void setSettings(List<Settings> settings) {
        this.settings = settings;
    }
}
