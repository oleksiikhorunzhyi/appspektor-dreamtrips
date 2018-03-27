package com.worldventures.dreamtrips.social.ui.profile.view.cell;

import android.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.service.profile.model.ReloadFeedModel;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_reload_feed)
public class ReloadFeedCell extends BaseAbstractDelegateCell<ReloadFeedModel, CellDelegate<ReloadFeedModel>> {

   public ReloadFeedCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      itemView.setVisibility(getModelObject().isVisible() ? View.VISIBLE : View.GONE);
      ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
      layoutParams.height = getModelObject().isVisible() ? ActionBar.LayoutParams.WRAP_CONTENT : 0;
      itemView.setLayoutParams(layoutParams);
   }

   @OnClick(R.id.profile_feed_reload)
   protected void onProfileFeedReload() {
      itemView.setVisibility(View.GONE);
      if (cellDelegate != null) {
         cellDelegate.onCellClicked(getModelObject());
      }
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
