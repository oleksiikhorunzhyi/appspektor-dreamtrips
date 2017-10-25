package com.worldventures.dreamtrips.modules.dtl.view.cell.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter;
import com.worldventures.core.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.WorkingHoursCell;

public class MerchantWorkingHoursAdapter extends BaseArrayListAdapter<OperationDay> {

   private final MerchantAttributes merchantAttributes;

   public MerchantWorkingHoursAdapter(Context context, MerchantAttributes merchantAttributes, Injector injector) {
      super(context, injector);
      this.merchantAttributes = merchantAttributes;
   }

   @Override
   public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
      AbstractCell cell = super.onCreateViewHolder(parent, viewType);
      if (shouldMerchantSet(cell)) {
         bindMerchantHolder(cast(cell));
      }
      return cell;
   }

   private WorkingHoursCell cast(AbstractCell cell) {
      return (WorkingHoursCell) cell;
   }

   private boolean shouldMerchantSet(AbstractCell cell) {
      return cell instanceof WorkingHoursCell;
   }

   private void bindMerchantHolder(WorkingHoursCell cell) {
      cell.setTimezone(merchantAttributes.timeOffset());
   }
}
