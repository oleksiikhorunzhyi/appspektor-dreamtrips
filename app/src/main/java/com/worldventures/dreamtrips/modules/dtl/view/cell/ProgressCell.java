package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;

@Layout(R.layout.adapter_item_progress)
public class ProgressCell extends BaseAbstractCell<ProgressCell.Model> {

   public ProgressCell(View view) {
      super(view);
   }

   public static final ProgressCell.Model INSTANCE = new ProgressCell.Model();

   @Override
   public boolean shouldInject() {
      return false;
   }

   @Override
   protected void syncUIStateWithModel() {

   }

   @Override
   public void prepareForReuse() {
   }

   public static final class Model {

      private Model() {
      }
   }
}