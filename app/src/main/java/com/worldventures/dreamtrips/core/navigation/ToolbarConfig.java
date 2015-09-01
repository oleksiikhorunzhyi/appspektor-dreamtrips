package com.worldventures.dreamtrips.core.navigation;

import java.io.Serializable;

public class ToolbarConfig implements Serializable {

    private boolean visible = true;
    private float alpha = 1.0f;

    public boolean isVisible() {
        return visible;
    }

    public float getAlpha() {
        return alpha;
    }

    public static class Builder {

        private ToolbarConfig toolbarConfig;

        public static ToolbarConfig.Builder create() {
            Builder builder = new ToolbarConfig.Builder();
            builder.toolbarConfig = new ToolbarConfig();
            return builder;
        }

        public ToolbarConfig.Builder visible(boolean visible) {
            toolbarConfig.visible = visible;
            return this;
        }

        public ToolbarConfig.Builder apha(float alpha) {
            toolbarConfig.alpha = alpha;
            return this;
        }

        public ToolbarConfig build() {
            return toolbarConfig;
        }
    }
}
