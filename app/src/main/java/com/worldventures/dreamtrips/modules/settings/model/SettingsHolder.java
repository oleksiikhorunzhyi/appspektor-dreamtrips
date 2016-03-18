package com.worldventures.dreamtrips.modules.settings.model;

import java.util.ArrayList;
import java.util.List;

public class SettingsHolder {

    private List<Setting> settings;

    public SettingsHolder(List<Setting> settings) {
        this.settings = settings;
    }

    public List<Setting> getSettings() {
        if (settings == null)
            return new ArrayList<>();
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }
}
