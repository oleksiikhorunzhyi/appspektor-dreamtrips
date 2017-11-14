package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_load_error)
public class MerchantsErrorCell extends BaseAbstractDelegateCell<MerchantsErrorCell.Model, CellDelegate<MerchantsErrorCell.Model>> {

   public static final MerchantsErrorCell.Model INSTANCE = new MerchantsErrorCell.Model();

   public MerchantsErrorCell(View view) {
      super(view);
   }

   @OnClick(R.id.retry)
   protected void onRetryClick() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   @Override
   protected void syncUIStateWithModel() {
      //do nothing
   }

   @Override
   public void prepareForReuse() {
      //do nothing
   }

   public static final class Model {

      private Model() {
      }
   }
}
