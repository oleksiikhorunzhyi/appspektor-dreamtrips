package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;

@Layout(R.layout.screen_dtl_map)
public class DtlMapPath extends DtlDetailPath {

    private final boolean toolbarCollapsed;

    public DtlMapPath(MasterDetailPath master, boolean toolbarCollapsed) {
        super(master);
        this.toolbarCollapsed = toolbarCollapsed;
    }

    public DtlMapPath(MasterDetailPath master) {
        super(master);
        this.toolbarCollapsed = true;
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITH_DRAWER;
    }
}
