package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.dreamtrips.R;

import java.util.concurrent.TimeUnit;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_dtl_location_header_cell)
public class DtlLocationSearchHeaderCell extends AbstractDelegateCell<DtlLocationSearchHeaderCell.HeaderModel, CellDelegate<DtlLocationSearchHeaderCell.HeaderModel>> {

   @InjectView(R.id.autoDetectNearMe) Button autoDetectNearMe;

   public DtlLocationSearchHeaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      RxView.clicks(autoDetectNearMe)
            .compose(RxLifecycleAndroid.bindView(itemView))
            .throttleFirst(3L, TimeUnit.SECONDS)
            .subscribe(aVoid -> cellDelegate.onCellClicked(getModelObject()));

   }

   @Override
   public void prepareForReuse() {
   }

   public static final class HeaderModel {

      private HeaderModel() {
      }

      public static final HeaderModel INSTANCE = new HeaderModel();
   }
}
