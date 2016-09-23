package com.worldventures.dreamtrips.modules.dtl.view.cell.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;

import javax.inject.Inject;

public class ThinMerchantsAdapter extends BaseDelegateAdapter<OperationDay> {

   @Inject SnappyRepository db;
   private final DistanceType distanceType;

   public ThinMerchantsAdapter(Context context, Injector injector) {
      super(context, injector);
      this.distanceType = MerchantHelper.getDistanceTypeFromSettings(db);
   }

   @Override
   public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
      AbstractCell cell = super.onCreateViewHolder(parent, viewType);
      if (shouldMerchantSet(cell)) {
         bindMerchantHolder(cast(cell));
      }
      return cell;
   }

   private DtlMerchantExpandableCell cast(AbstractCell cell) {
      return (DtlMerchantExpandableCell) cell;
   }

   private boolean shouldMerchantSet(AbstractCell cell) {
      return cell instanceof DtlMerchantExpandableCell;
   }

   private void bindMerchantHolder(DtlMerchantExpandableCell cell) {
      cell.setDistanceType(distanceType);
   }
}
