package com.worldventures.dreamtrips.social.ui.friends.view.cell;

import android.text.TextUtils;
import android.view.View;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.DrawableUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.FriendCellDelegate;
import com.worldventures.dreamtrips.social.ui.profile.view.dialog.FriendActionDialogDelegate;

import javax.inject.Inject;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_friend)
public class FriendCell extends BaseUserCell<FriendCellDelegate> {

   @Inject DrawableUtil drawableUtil;

   public FriendCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      String circleName = getModelObject().getCirclesString();
      tvGroup.setVisibility(TextUtils.isEmpty(circleName) ? View.GONE : View.VISIBLE);
      tvGroup.setText(circleName);
   }

   @Override
   public void afterInject() {
      super.afterInject();
   }

   @OnClick(R.id.tv_actions)
   public void onAction() {
      sdvAvatar.setDrawingCacheEnabled(true);
      new FriendActionDialogDelegate(itemView.getContext())
            .onFriendPrefsAction(cellDelegate::onOpenPrefs)
            .onStartSingleChatAction(cellDelegate::onStartSingleChat)
            .onUnfriend(this::onUnfriend)
            .showFriendDialog(getModelObject(), drawableUtil.copyIntoDrawable(sdvAvatar.getDrawingCache()));
   }

   private void onUnfriend(User user) {
      cellDelegate.onUnfriend(user);
   }
}
