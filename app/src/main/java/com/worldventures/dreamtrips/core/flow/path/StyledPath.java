package com.worldventures.dreamtrips.core.flow.path;

public abstract class StyledPath extends MasterDetailPath {

    public PathAttrs getAttrs() {
        return WITHOUT_DRAWER;
    }

    public static class PathAttrs {

        private boolean drawerEnabled;

        public boolean isDrawerEnabled() {
            return drawerEnabled;
        }

        public static PathAttrs withDrawer(boolean enabled) {
            PathAttrs pathAttrs = new PathAttrs();
            pathAttrs.drawerEnabled = enabled;
            return pathAttrs;
        }
    }

    protected static final PathAttrs WITH_DRAWER = PathAttrs.withDrawer(true);
    protected static final PathAttrs WITHOUT_DRAWER = PathAttrs.withDrawer(false);
}
