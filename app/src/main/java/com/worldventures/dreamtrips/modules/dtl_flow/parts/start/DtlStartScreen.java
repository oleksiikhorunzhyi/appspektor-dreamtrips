package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlStartScreen extends DtlScreen {

    void locationResolutionRequired(Status status);

    void showProgress();

    void hideProgress();
}
