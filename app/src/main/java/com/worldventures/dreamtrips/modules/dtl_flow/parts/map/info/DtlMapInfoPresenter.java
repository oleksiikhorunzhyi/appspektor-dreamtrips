package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlMapInfoPresenter extends DtlPresenter<DtlMapInfoScreen, ViewState.EMPTY> {

    void onMarkerClick();

    void onSizeReady(int height);
}
