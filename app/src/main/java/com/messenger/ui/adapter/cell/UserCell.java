package com.messenger.ui.adapter.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.entities.DataUser;
import com.messenger.ui.widget.AvatarView;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

public abstract class UserCell<T, D extends CellDelegate<T>> extends BaseAbstractDelegateCell<T, D> {

   @InjectView(R.id.contact_icon) AvatarView avatarView;
   @InjectView(R.id.contact_name_textview) TextView nameTextView;
   @InjectView(R.id.contact_chat_tick_image_view) ImageView tickImageView;

   public UserCell(View view) {
      super(view);
   }

   public ImageView getTickImageView() {
      return tickImageView;
   }

   @Override
   protected void syncUIStateWithModel() {
      DataUser user = getDataUser();
      nameTextView.setText(user.getName());
      setUserOnline(user);
      avatarView.setImageURI(Uri.parse(user.getAvatarUrl()));
   }

   protected void setUserOnline(DataUser user) {
      avatarView.setOnline(user.isOnline());
   }

   protected abstract DataUser getDataUser();

   @Override
   public boolean shouldInject() {
      return false;
   }
}
