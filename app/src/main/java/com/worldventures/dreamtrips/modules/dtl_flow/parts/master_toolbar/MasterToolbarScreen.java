package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface MasterToolbarScreen extends DtlScreen {

    void updateToolbarTitle(@Nullable DtlLocation dtlLocation, @Nullable String appliedSearchQuery);

    void toggleDiningFilterSwitch(boolean enabled);

    void setFilterButtonState(boolean enabled);
}
