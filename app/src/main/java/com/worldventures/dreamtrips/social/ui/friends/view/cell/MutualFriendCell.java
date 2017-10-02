package com.worldventures.dreamtrips.social.ui.friends.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.profile.view.dialog.FriendActionDialogDelegate;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_mutual)
public class MutualFriendCell extends FriendCell {

   public MutualFriendCell(View view) {
      super(view);
   }

   @OnClick(R.id.tv_actions)
   public void onAction() {
      sdvAvatar.setDrawingCacheEnabled(true);
      new FriendActionDialogDelegate(itemView.getContext())
            .onFriendPrefsAction(cellDelegate::onOpenPrefs)
            .onUnfriend(cellDelegate::onUnfriend)
            .showFriendDialogSkipChat(getModelObject(), drawableUtil.copyIntoDrawable(sdvAvatar.getDrawingCache()));
   }
}
