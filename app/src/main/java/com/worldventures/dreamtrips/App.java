package com.worldventures.dreamtrips;

import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.core.module.DTModule;

public class App extends BaseApplicationWithInjector {

    @Override
    protected Object getApplicationModule() {
        return new DTModule(this);
    }

}