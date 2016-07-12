package com.worldventures.dreamtrips.core.flow.path;

import android.support.v7.app.AppCompatActivity;

public abstract class StyledPath extends MasterDetailPath implements AttributedPath {

    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public void onPreDispatch(AppCompatActivity activity) {
        //
    }
}
