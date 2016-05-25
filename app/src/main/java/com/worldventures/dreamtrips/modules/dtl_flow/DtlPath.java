package com.worldventures.dreamtrips.modules.dtl_flow;

import android.support.v7.app.AppCompatActivity;

import com.worldventures.dreamtrips.core.flow.path.AttributedPath;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;

public abstract class DtlPath extends MasterDetailPath implements AttributedPath {

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITHOUT_DRAWER;
    }

    @Override
    public void onPreDispatch(AppCompatActivity activity) {
    }
}
