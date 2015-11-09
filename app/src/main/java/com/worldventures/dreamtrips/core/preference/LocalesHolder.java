package com.worldventures.dreamtrips.core.preference;

import com.google.gson.reflect.TypeToken;
import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;

import java.util.ArrayList;

public class LocalesHolder extends ComplexObjectStorage<ArrayList<AvailableLocale>> {

    public LocalesHolder(SimpleKeyValueStorage storage) {
        super(storage, "locales", new TypeToken<ArrayList<AvailableLocale>>(){}.getType());
    }
}
