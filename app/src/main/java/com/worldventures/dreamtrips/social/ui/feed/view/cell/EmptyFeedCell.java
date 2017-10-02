package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.feed.model.cell.EmptyFeedModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_empty_feed)
public class EmptyFeedCell extends BaseAbstractDelegateCell<EmptyFeedModel, CellDelegate<EmptyFeedModel>> {

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
