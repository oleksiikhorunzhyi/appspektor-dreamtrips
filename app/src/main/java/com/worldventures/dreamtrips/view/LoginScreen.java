package com.worldventures.dreamtrips.view;

import mortar.Blueprint;

public class LoginScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    private class Module {

    }
}
