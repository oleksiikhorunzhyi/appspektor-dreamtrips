package com.messenger.ui.adapter.inflater.conversation;

import android.view.View;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.ui.adapter.inflater.ViewInflater;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.SwipeClickListener;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ConversationSwipeLayoutInflater extends ViewInflater implements View.OnClickListener {

   @InjectView(R.id.swipe) SwipeLayout swipeLayout;
   @InjectView(R.id.swipe_layout_button_delete) View deleteButton;
   @InjectView(R.id.swipe_layout_button_more) View moreButton;

   private ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener;

   private DataConversation conversation;

   public void setView(View rootView, View.OnClickListener itemViewClickListener) {
      super.setView(rootView);

      deleteButton.setOnClickListener(this);
      moreButton.setOnClickListener(this);

      //// TODO: 1/11/16 enable swipe and use comments below for future functional
      swipeLayout.setSwipeEnabled(false);

      SwipeClickListener swipeClickListener = new SwipeClickListener(rootView, itemViewClickListener);
      swipeLayout.addSwipeListener(swipeClickListener);
   }

   public void bind(DataConversation conversation, DataUser currentUser) {
      this.conversation = conversation;
      // TODO: 1/2/16 remove checking conversation type
      if (ConversationHelper.isGroup(conversation) && !ConversationHelper.isOwner(conversation, currentUser)) {
         deleteButton.setVisibility(View.VISIBLE);
      } else {
         deleteButton.setVisibility(View.GONE);
      }
   }

   public void setSwipeButtonsListener(ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener) {
      this.swipeButtonsListener = swipeButtonsListener;
   }

   @Override
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.swipe_layout_button_more:
            if (swipeButtonsListener != null) swipeButtonsListener.onMoreOptionsButtonPressed(conversation);
            break;
         case R.id.swipe_layout_button_delete:
            if (swipeButtonsListener != null) swipeButtonsListener.onDeleteButtonPressed(conversation);
            break;
      }
   }
}
