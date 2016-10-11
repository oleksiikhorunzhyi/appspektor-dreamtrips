package com.worldventures.dreamtrips.modules.dtl.view.cell.delegates;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlMerchantExpandableCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.MerchantsErrorCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.ProgressCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.ThinMerchantsAdapter;

import java.util.List;

public class MerchantsAdapterDelegate {

   private ThinMerchantsAdapter adapter;

   public void setup(ThinMerchantsAdapter adapter) {
      this.adapter = adapter;
      registerBaseCells();
   }

   public void registerDelegate(Class<?> itemClass, CellDelegate<?> cellDelegate) {
      adapter.registerDelegate(itemClass, cellDelegate);
   }

   private void registerBaseCells() {
      adapter.registerCell(ImmutableThinMerchant.class, DtlMerchantExpandableCell.class);
      adapter.registerCell(ProgressCell.Model.class, ProgressCell.class);
      adapter.registerCell(MerchantsErrorCell.Model.class, MerchantsErrorCell.class);
   }

   public void toggleItem(boolean expand, ThinMerchant merchant) {
      adapter.toggle(expand, merchant);
   }

   public void setItems(List<ThinMerchant> items) {
      adapter.setItems(items);
   }

   public void addItem(Object item) {
      adapter.addItem(item);
      adapter.notifyItemInserted(getItems().size() - 1);
   }

   public void removeItem(Object item) {
      adapter.remove(item);
   }

   public List getItems() {
      return adapter.getItems();
   }

   public void clear() {
      adapter.clear();
   }

   public boolean isItemsPresent() {
      return !getItems().isEmpty();
   }

   public void setExpandedMerchants(List<String> ids) {
      adapter.setExpandedMerchantIds(ids);
   }

   public List<String> getExpandedMerchants() {
      return adapter.getExpandedMerchantIds();
   }
}

