package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import java.util.List;

public interface FilterView extends MvpView {

   Injector getInjector();

   void toggleDrawer(boolean show);

   void applyFilterState(FilterData filterData);

   void showAmenitiesItems(List<Attribute> amenities, FilterData filterData);

   void showAmenitiesListProgress();

   void showAmenitiesError();

   FilterData getFilterData();
}
