package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.ImmutableOperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.WorkingHoursCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.MerchantWorkingHoursAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.custom.ExpandableOfferView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MerchantWorkingHoursInflater extends MerchantDataInflater {

   @InjectView(R.id.expandedWorkingHoursView) ExpandableOfferView expandedView;
   protected RecyclerView hoursRecyclerView;
   protected BaseArrayListAdapter adapter;

   private final Injector injector;

   public MerchantWorkingHoursInflater(Injector injector) {
      this.injector = injector;
   }

   @Override
   public void setView(View rootView) {
      super.setView(rootView);
      //
      hoursRecyclerView = ButterKnife.findById(expandedView, R.id.workingHoursView);
      hoursRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
      hoursRecyclerView.setNestedScrollingEnabled(false);
   }

   @Override
   protected void onMerchantAttributesApply() {
      if (!merchantAttributes.hasOperationDays()) {
         ViewUtils.setViewVisibility(expandedView, View.GONE);
      } else {
         adapter = new MerchantWorkingHoursAdapter(rootView.getContext(), merchantAttributes, injector);
         adapter.registerCell(ImmutableOperationDay.class, WorkingHoursCell.class);
         adapter.setItems(merchantAttributes.operationDays());
         hoursRecyclerView.setAdapter(adapter);
      }
   }

   public boolean isViewExpanded() {
      return expandedView.isOpened();
   }

   public void preexpand() {
      expandedView.showWithoutAnimation();
   }
}
