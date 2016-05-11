package com.worldventures.dreamtrips.core.flow.path;

public class PathAttrs {

    private boolean drawerEnabled;

    public boolean isDrawerEnabled() {
        return drawerEnabled;
    }

    public static PathAttrs withDrawer(boolean enabled) {
        PathAttrs pathAttrs = new PathAttrs();
        pathAttrs.drawerEnabled = enabled;
        return pathAttrs;
    }

    public static final PathAttrs WITH_DRAWER = PathAttrs.withDrawer(true);
    public static final PathAttrs WITHOUT_DRAWER = PathAttrs.withDrawer(false);
}
