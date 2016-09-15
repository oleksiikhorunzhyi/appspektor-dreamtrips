package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_mutual)
public class MutualFriendCell extends FriendCell {

   public MutualFriendCell(View view) {
      super(view);
   }

   @OnClick(R.id.tv_actions)
   public void onAction() {
      sdvAvatar.setDrawingCacheEnabled(true);
      dialog.showFriendDialogSkipChat(getModelObject(), drawableUtil.copyIntoDrawable(sdvAvatar.getDrawingCache()));
   }
}
