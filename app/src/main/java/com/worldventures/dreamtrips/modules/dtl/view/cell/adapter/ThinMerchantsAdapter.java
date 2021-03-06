package com.worldventures.dreamtrips.modules.dtl.view.cell.adapter;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ThinMerchantsAdapter extends BaseDelegateAdapter {

   @Inject SettingsStorage db;
   private final DistanceType distanceType;

   private List<String> expandedMerchantIds = new ArrayList<>();

   public void setExpandedMerchantIds(List<String> expandedMerchantIds) {
      this.expandedMerchantIds = expandedMerchantIds;
   }

   public List<String> getExpandedMerchantIds() {
      return expandedMerchantIds;
   }

   public ThinMerchantsAdapter(Context context, Injector injector) {
      super(context, injector);
      this.injector.inject(this);
      this.distanceType = FilterHelper.provideDistanceFromSettings(db);
   }

   public void toggle(boolean expand, ThinMerchant merchant) {
      if (expand) {
         expandedMerchantIds.add(merchant.id());
      } else {
         expandedMerchantIds.remove(merchant.id());
      }

      updateItem(merchant);
   }

   @Override
   public void onBindViewHolder(AbstractCell cell, int position) {
      if (cell instanceof DtlMerchantExpandableCell) {
         DtlMerchantExpandableCell holder = (DtlMerchantExpandableCell) cell;
         holder.setDistanceType(distanceType);
         holder.setExpanded(false);
      }
      super.onBindViewHolder(cell, position);
   }
}
