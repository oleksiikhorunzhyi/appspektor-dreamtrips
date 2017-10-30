package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;

@Layout(R.layout.adapter_item_progress)
public class ProgressCell extends BaseAbstractCell<ProgressCell.Model> {

   public static final ProgressCell.Model INSTANCE = new ProgressCell.Model();

   public ProgressCell(View view) {
      super(view);
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
