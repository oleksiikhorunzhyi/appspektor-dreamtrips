package com.worldventures.dreamtrips.core.preference;

import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;

public class StaticPageHolder extends ComplexObjectStorage<StaticPageConfig> {

    public StaticPageHolder(SimpleKeyValueStorage storage) {
        super(storage, "STATIC_PAGE", StaticPageConfig.class);
    }
}
