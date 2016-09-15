package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;

public interface FilterView extends MvpView {

   Injector getInjector();

   void toggleDrawer(boolean show);

   /**
    * Return {@link DtlFilterParameters} with current filter state
    *
    * @return DtlFilterParameters filter state
    */
   DtlFilterParameters getFilterParameters();

   /**
    * Update UI state with given filter parameters
    *
    * @param filterData dataSet to map to UI
    */
   void syncUi(DtlFilterData filterData);
}
