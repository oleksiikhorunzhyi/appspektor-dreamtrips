package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_item_progress)
public class ProgressCell extends AbstractCell<ProgressCell.Model> {

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