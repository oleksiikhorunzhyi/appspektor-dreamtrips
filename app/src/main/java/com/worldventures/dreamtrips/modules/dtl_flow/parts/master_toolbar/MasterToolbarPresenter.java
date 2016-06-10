package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface MasterToolbarPresenter
        extends DtlPresenter<MasterToolbarScreen, ViewState.EMPTY> {

    void applySearch(String query);

    void applyOffersOnlyFilterState(boolean enabled);
}
