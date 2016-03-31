package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.StyledPath;

@Layout(R.layout.screen_dtl_map)
public class DtlMapPath extends StyledPath{

    @Override
    public PathAttrs getAttrs() {
        return WITH_DRAWER;
    }
}
