package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlStartPresenter extends DtlPresenter<DtlStartScreen, ViewState.EMPTY> {

    public void onLocationResolutionGranted();

    public void onLocationResolutionDenied();
}
