package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowScreen;

public interface DtlStartScreen extends FlowScreen {

    void locationResolutionRequired(Status status);

    void showProgress();

    void hideProgress();
}
