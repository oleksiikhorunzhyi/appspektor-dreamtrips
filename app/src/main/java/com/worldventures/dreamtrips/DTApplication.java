package com.worldventures.dreamtrips;

import android.app.Application;

import mortar.Mortar;
import mortar.MortarScope;

public class DTApplication extends Application {
    private MortarScope applicationScope;

    @Override public void onCreate() {
        super.onCreate();
        // Eagerly validate development builds (too slow for production).
        applicationScope = Mortar.createRootScope(BuildConfig.DEBUG);
    }

    @Override public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return applicationScope;
        }
        return super.getSystemService(name);
    }
}
