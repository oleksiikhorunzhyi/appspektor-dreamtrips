package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlStartPresenter extends FlowPresenter<DtlStartScreen, ViewState.EMPTY> {

    public void onLocationResolutionGranted();

    public void onLocationResolutionDenied();
}
