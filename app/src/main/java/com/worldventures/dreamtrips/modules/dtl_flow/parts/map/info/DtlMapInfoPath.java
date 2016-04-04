package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsPath;

@Layout(R.layout.fragment_dtl_map_info)
public class DtlMapInfoPath extends DtlDetailsPath {

    public DtlMapInfoPath(@NonNull String id) {
        super(id);
    }

    @Override
    public PathAttrs getAttrs() {
        return WITHOUT_DRAWER;
    }
}
