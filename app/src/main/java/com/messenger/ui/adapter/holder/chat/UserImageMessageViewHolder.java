package com.messenger.ui.adapter.holder.chat;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.OnClick;
import butterknife.OnLongClick;

@Layout(R.layout.list_item_chat_user_image_message)
public class UserImageMessageViewHolder extends ImageMessageViewHolder {

   public UserImageMessageViewHolder(View itemView) {
      super(itemView);
   }

   @OnClick(R.id.chat_image_error)
   void onMessageErrorClicked() {
      loadImage();
   }

   @OnLongClick(R.id.chat_image_post_image_view)
   boolean onImageLongClick() {
      return super.onMessageLongClicked();
   }
}