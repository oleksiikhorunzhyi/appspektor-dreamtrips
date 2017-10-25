package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.LoadMore;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_more_comments)
public class LoadMoreCell extends BaseAbstractDelegateCell<LoadMore, CellDelegate<LoadMore>> {

   @InjectView(R.id.caption) TextView caption;

   public LoadMoreCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      itemView.setOnClickListener(view -> {
         if (!getModelObject().isLoading()) {
            cellDelegate.onCellClicked(getModelObject());
         }
      });

      caption.setText(getModelObject().isLoading() ? R.string.loading : R.string.comment_view_more);
      caption.setVisibility(getModelObject().isVisible() ? View.VISIBLE : View.GONE);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
