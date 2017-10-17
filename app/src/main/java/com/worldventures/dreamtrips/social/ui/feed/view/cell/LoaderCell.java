package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.LoadMore;

@Layout(R.layout.adapter_item_load_more_feed)
public class LoaderCell extends AbstractCell<LoadMore> {

   public LoaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      //do nothing
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
