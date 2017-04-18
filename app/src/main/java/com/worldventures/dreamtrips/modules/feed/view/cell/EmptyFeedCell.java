package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.feed.model.cell.EmptyFeedModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_empty_feed)
public class EmptyFeedCell extends AbstractDelegateCell<EmptyFeedModel, CellDelegate<EmptyFeedModel>> {

   @InjectView(R.id.arrow) ImageView ivArrow;
   @InjectView(R.id.tv_search_friends) TextView tvSearchFriends;

   public EmptyFeedCell(View view) {
      super(view);
      view.setOnClickListener(v -> cellDelegate.onCellClicked(null));
   }

   @Override
   public void fillWithItem(EmptyFeedModel item) {
      super.fillWithItem(item);
      tvSearchFriends.setPaintFlags(tvSearchFriends.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
      if (ViewUtils.isPhoneLandscape(itemView.getContext())) {
         ivArrow.setVisibility(View.GONE);
      }
   }

   @Override
   protected void syncUIStateWithModel() {

   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
