package com.worldventures.dreamtrips;

import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.core.module.DTModule;

import java.util.Arrays;
import java.util.List;

public class DTApplication extends BaseApplicationWithInjector {
    public List<Object> getModules() {
        return Arrays.asList(
                new DTModule(this)
        );
    }
}
