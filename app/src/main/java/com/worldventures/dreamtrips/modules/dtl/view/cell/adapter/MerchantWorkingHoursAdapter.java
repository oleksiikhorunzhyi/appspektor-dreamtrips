package com.worldventures.dreamtrips.modules.dtl.view.cell.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.WorkingHoursCell;

public class MerchantWorkingHoursAdapter extends BaseArrayListAdapter<OperationDay> {

   private final Merchant merchant;

   public MerchantWorkingHoursAdapter(Context context, Merchant merchant, Injector injector) {
      super(context, injector);
      this.merchant = merchant;
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
      cell.setTimezone(MerchantHelper.merchantTimeOffset(merchant.timeZone()));
   }
}
