package com.worldventures.dreamtrips.core.utils.events;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.component.ComponentDescription;

public class OpenMenuItemEvent {

    private ComponentDescription componentDescription;
    private Bundle args;

    public OpenMenuItemEvent(ComponentDescription componentDescription) {
        this(componentDescription, null);
    }

    public OpenMenuItemEvent(ComponentDescription componentDescription, Bundle args) {
        this.componentDescription = componentDescription;
        this.args = args;
    }

    public Bundle getArgs() {
        return args;
    }

    public ComponentDescription getComponentDescription() {
        return componentDescription;
    }
}
