package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.comment.LoadMore;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_more_comments)
public class LoadMoreCell extends AbstractDelegateCell<LoadMore, CellDelegate<LoadMore>> {

   @InjectView(R.id.caption) TextView caption;

   public LoadMoreCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));

      caption.setText(getModelObject().isLoading() ? R.string.loading : R.string.comment_view_more);
      caption.setVisibility(getModelObject().isVisible() ? View.VISIBLE : View.GONE);
   }
}
