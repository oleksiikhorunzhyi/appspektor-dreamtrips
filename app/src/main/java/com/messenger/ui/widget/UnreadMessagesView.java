package com.messenger.ui.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UnreadMessagesView extends CardView {

   @InjectView(R.id.chat_users_unread_messages_textview) TextView unreadMessagesTextView;

   private View.OnClickListener closeButtonClickListener;
   private View.OnClickListener unreadMessagesClickListener;

   public UnreadMessagesView(Context context) {
      super(context);
      init();
   }

   public UnreadMessagesView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      ButterKnife.inject(this, LayoutInflater.from(getContext()).inflate(R.layout.widget_unread_messages, this, true));
   }

   @OnClick(R.id.chat_close_icon)
   void closeButtonPressed(View v) {
      if (closeButtonClickListener != null) {
         closeButtonClickListener.onClick(v);
      }
   }

   public void setCloseButtonClickListener(OnClickListener closeButtonClickListener) {
      this.closeButtonClickListener = closeButtonClickListener;
   }

   @OnClick(R.id.chat_users_unread_messages_textview)
   public void unreadMessagesTextViewPressed(View v) {
      if (unreadMessagesClickListener != null) {
         unreadMessagesClickListener.onClick(v);
      }
   }

   public void setUnreadMessagesClickListener(OnClickListener unreadMessagesClickListener) {
      this.unreadMessagesClickListener = unreadMessagesClickListener;
   }

   public void show() {
      // possibly to be replaced with animation in the future
      setVisibility(View.VISIBLE);
   }

   public void hide() {
      // possibly to be replaced with animation in the future
      setVisibility(View.GONE);
   }

   public void updateCount(int unreadMessagesCount) {
      unreadMessagesTextView.setText(String.format(getResources().getString(R.string.chat_unread_messages_textview_format), unreadMessagesCount));
   }
}