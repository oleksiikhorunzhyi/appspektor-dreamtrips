package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

@Layout(R.layout.screen_dtl_map_info)
public class DtlMapInfoPath extends DtlMerchantDetailsPath {

    public DtlMapInfoPath(MasterDetailPath path, @NonNull String id) {
        super(path, id);
    }

    @Override
    public PathAttrs getAttrs() {
        return WITHOUT_DRAWER;
    }
}
