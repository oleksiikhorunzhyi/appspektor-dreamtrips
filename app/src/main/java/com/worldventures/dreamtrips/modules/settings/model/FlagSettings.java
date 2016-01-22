package com.worldventures.dreamtrips.modules.settings.model;

public class FlagSettings extends Settings<Boolean> {

    @Override
    public Boolean getValue() {
        return value == null ? false : value;
    }
}
