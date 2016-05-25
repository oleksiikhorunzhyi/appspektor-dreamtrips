package com.messenger.ui.adapter.inflater.chat;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataMessage$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.ChatCellDelegate;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.util.chat.ChatTimestampProvider;
import com.messenger.ui.util.chat.anim.SlideDownAnimator;
import com.messenger.ui.util.chat.anim.SlideUpAnimator;
import com.messenger.ui.util.chat.anim.TimestampAnimator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ChatTimestampInflater {

    private RecyclerView.Adapter adapter;
    @Inject
    ChatTimestampProvider timestampProvider;

    private List<TimestampAnimator> pendingAnimators = new ArrayList<>();
    private int manualTimestampPosition = -1;
    private PublishSubject<Integer> clickedTimestampPositionsObservable = PublishSubject.create();

    public ChatTimestampInflater(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public boolean bindTimeStampIfNeeded(MessageViewHolder holder, Cursor cursor, int position) {
        int messageStatus = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.STATUS));
        if (messageStatus == MessageStatus.ERROR) {
            holder.dateTextView.setVisibility(View.GONE);
            return false;
        }

        boolean manualTimestamp = manualTimestampPosition == position;
        boolean automaticTimestamp = timestampProvider.shouldShowAutomaticTimestamp(cursor);

        holder.getTimestampClickableView().setOnClickListener(view -> {
            if ((manualTimestamp || !automaticTimestamp)) {
                clickedTimestampPositionsObservable.onNext(position);
            }

        });

        TextView dateTextView = holder.dateTextView;
        if (!runPendingAnimations(cursor, position, dateTextView)) {
            bindNonAnimatedTimestampIfNeeded(timestampProvider.getTimestamp(cursor),
                    manualTimestamp, automaticTimestamp, dateTextView);
        }

        return position == manualTimestampPosition;
    }

    private boolean runPendingAnimations(Cursor cursor, int position, TextView dateTextView) {
        for (Iterator<TimestampAnimator> it = pendingAnimators.iterator(); it.hasNext(); ) {
            TimestampAnimator timestampAnimator = it.next();
            if (timestampAnimator.getPosition() == position) {
                dateTextView.setText(timestampProvider.getTimestamp(cursor));
                timestampAnimator.setDateTextView(dateTextView);
                timestampAnimator.start();
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void showManualTimestampForPosition(int position) {
        if (position == manualTimestampPosition) {
            removeTimestamp();
        } else {
            addTimestamp(position);
        }
    }

    private void removeTimestamp() {
        pendingAnimators.add(new SlideDownAnimator(manualTimestampPosition));
        adapter.notifyItemChanged(manualTimestampPosition);
        manualTimestampPosition = -1;
    }

    private void addTimestamp(int position) {
        // hide previous opened timestamp
        if (manualTimestampPosition != -1) {
            pendingAnimators.add(new SlideDownAnimator(manualTimestampPosition));
            adapter.notifyItemChanged(manualTimestampPosition);
        }

        manualTimestampPosition = position;
        pendingAnimators.add(new SlideUpAnimator(manualTimestampPosition));
        adapter.notifyItemChanged(manualTimestampPosition);
    }

    private void bindNonAnimatedTimestampIfNeeded(String timeStamp, boolean manualTimestamp,
                                                    boolean automaticTimestamp, TextView dateTextView) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
        params.bottomMargin = 0;
        if (automaticTimestamp || manualTimestamp) {
            dateTextView.setVisibility(View.VISIBLE);
            dateTextView.setText(timeStamp);
        } else {
            dateTextView.setVisibility(View.GONE);
        }
    }

    public PublishSubject<Integer> getClickedTimestampPositionsObservable() {
        return clickedTimestampPositionsObservable;
    }
}
