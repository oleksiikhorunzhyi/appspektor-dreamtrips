package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;

@Layout(R.layout.adapter_item_load_more_feed)
public class LoaderCell extends AbstractCell<LoadMore> {

   public LoaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {}

   @Override
   public boolean shouldInject() {
      return false;
   }
}
