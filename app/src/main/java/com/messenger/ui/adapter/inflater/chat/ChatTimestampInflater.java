package com.messenger.ui.adapter.inflater.chat;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataMessage$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.util.chat.ChatTimestampProvider;
import com.messenger.ui.util.chat.anim.TimestampAnimationType;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.subjects.PublishSubject;

public class ChatTimestampInflater {

   private RecyclerView.Adapter adapter;

   ChatTimestampProvider timestampProvider;

   @Inject
   public ChatTimestampInflater(ChatTimestampProvider timestampProvider) {
      this.timestampProvider = timestampProvider;
   }

   private Map<Integer, TimestampAnimationType> pendingAnimations = new HashMap<>();
   private int manualTimestampPosition = -1;
   private PublishSubject<Integer> clickedTimestampPositionsObservable = PublishSubject.create();

   public void setAdapter(RecyclerView.Adapter adapter) {
      this.adapter = adapter;
   }

   //Note: positionInCursor != positionInAdapter, because there might be headers
   public boolean bindTimeStampIfNeeded(MessageViewHolder holder, Cursor cursor, int positionInCursor, int headerCount) {
      int positionInAdapter = positionInCursor + headerCount;

      int messageStatus = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.STATUS));
      if (messageStatus == MessageStatus.ERROR) {
         holder.dateTextView.setVisibility(View.GONE);
         return false;
      }

      boolean manualTimestamp = manualTimestampPosition == positionInAdapter;
      boolean automaticTimestamp = timestampProvider.shouldShowAutomaticTimestamp(cursor);

      holder.getTimestampClickableView().setOnClickListener(view -> {
         if ((manualTimestamp || !automaticTimestamp)) {
            clickedTimestampPositionsObservable.onNext(positionInAdapter);
         }
      });

      TimestampAnimationType pendingAnimationType = pendingAnimations.get(positionInAdapter);
      bindNonAnimatedTimestampIfNeeded(pendingAnimationType != null, timestampProvider.getTimestamp(cursor), manualTimestamp, automaticTimestamp, holder.dateTextView);

      return positionInAdapter == manualTimestampPosition;
   }

   public void showManualTimestampForPosition(int position) {
      if (position == manualTimestampPosition) {
         removeTimestamp();
      } else {
         addTimestamp(position);
      }
   }

   private void removeTimestamp() {
      pendingAnimations.put(manualTimestampPosition, TimestampAnimationType.SLIDE_DOWN);
      adapter.notifyItemChanged(manualTimestampPosition);
      manualTimestampPosition = -1;
   }

   private void addTimestamp(int position) {
      // hide previous opened timestamp
      if (manualTimestampPosition != -1) {
         pendingAnimations.put(manualTimestampPosition, TimestampAnimationType.SLIDE_DOWN);
         adapter.notifyItemChanged(manualTimestampPosition);
      }

      manualTimestampPosition = position;
      pendingAnimations.put(manualTimestampPosition, TimestampAnimationType.SLIDE_UP);
      adapter.notifyItemChanged(manualTimestampPosition);
   }

   private void bindNonAnimatedTimestampIfNeeded(boolean pendingAnimation, String timeStamp, boolean manualTimestamp, boolean automaticTimestamp, TextView dateTextView) {
      dateTextView.setText(timeStamp);
      if (pendingAnimation) {
         return;
      }
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
      params.bottomMargin = 0;
      if ((automaticTimestamp || manualTimestamp)) {
         dateTextView.setVisibility(View.VISIBLE);
      } else {
         dateTextView.setVisibility(View.GONE);
      }
   }

   public PublishSubject<Integer> getClickedTimestampPositionsObservable() {
      return clickedTimestampPositionsObservable;
   }

   public TimestampAnimationType popPendingAnimation(int position) {
      return pendingAnimations.remove(position);
   }
}
