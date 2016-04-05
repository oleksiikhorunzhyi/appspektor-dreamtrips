package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.StyledPath;

@Layout(R.layout.screen_dtl_details)
public class DtlDetailsPath extends StyledPath {

    private final String id;

    public DtlDetailsPath(@NonNull String id) {
        if (id == null || TextUtils.isEmpty(id))
            throw new IllegalArgumentException("Merchant's \'id\' argument can not be null or empty");
        //
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public PathAttrs getAttrs() {
        return WITHOUT_DRAWER;
    }
}
