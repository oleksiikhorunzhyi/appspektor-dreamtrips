package com.messenger.ui.util.chat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.entities.DataMessage$Table;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.anim.SimpleAnimatorListener;

import javax.inject.Inject;

public class ChatTimestampAnimator {

    private RecyclerView.Adapter adapter;
    @Inject
    ChatTimestampProvider timestampProvider;

    private int manualTimestampPositionToAdd = -1;
    private int manualTimestampPosition = -1;
    private int manualTimestampPositionToRemove = -1;
    private boolean manualTimestampAdditionIsPending;

    public ChatTimestampAnimator(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public boolean isManualTimestampPosition(int position) {
        return position == manualTimestampPosition;
    }

    public void bindTimeStampIfNeeded(MessageViewHolder holder, Cursor cursor, int position) {
        int messageStatus = cursor.getInt(cursor.getColumnIndex(DataMessage$Table.STATUS));
        if (messageStatus == MessageStatus.ERROR) {
            holder.dateTextView.setVisibility(View.GONE);
            return;
        }

        boolean manualTimestamp = manualTimestampPosition == position;
        boolean automaticTimestamp = timestampProvider.shouldShowAutomaticTimestamp(cursor);

        holder.getTimestampClickableView().setOnClickListener(view -> {
            if (manualTimestamp || !automaticTimestamp) {
                showManualTimestampForPosition(position);
            }
        });

        TextView dateTextView = holder.dateTextView;

        if (manualTimestampPositionToAdd == position) {
            manualTimestampAdditionIsPending = true;
            manualTimestampPositionToAdd = -1;
            manualTimestampPosition = position;

            dateTextView.setVisibility(View.VISIBLE);
            dateTextView.setText(timestampProvider.getTimestamp(cursor));

            if (dateTextView.getMeasuredHeight() == 0) {
                dateTextView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            }

            ValueAnimator animator = ValueAnimator.ofFloat(-dateTextView.getMeasuredHeight(), 0);
            animator.addUpdateListener(new BottomMarginAnimationListener(dateTextView));
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    manualTimestampAdditionIsPending = false;
                }
            });
            animator.start();
        } else if (manualTimestampPositionToRemove == position) {
            manualTimestampPositionToRemove = -1;
            // ensure there is no pending add operation
            if (!manualTimestampAdditionIsPending) {
                manualTimestampPosition = -1;
            }
            ValueAnimator animator = ValueAnimator.ofFloat(0, -dateTextView.getMeasuredHeight());
            animator.addUpdateListener(new BottomMarginAnimationListener(dateTextView));
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    dateTextView.setVisibility(View.GONE);
                }
            });
            animator.start();
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
            params.bottomMargin = 0;
            if (automaticTimestamp || manualTimestamp) {
                dateTextView.setVisibility(View.VISIBLE);
                dateTextView.setText(timestampProvider.getTimestamp(cursor));
            } else {
                dateTextView.setVisibility(View.GONE);
            }
        }
    }

    private void showManualTimestampForPosition(int position) {
        if (position == manualTimestampPosition) {
            manualTimestampPositionToRemove = manualTimestampPosition;
            adapter.notifyItemChanged(position);
        } else {
            int prevPosition = manualTimestampPosition;
            manualTimestampPositionToAdd = position;
            manualTimestampPositionToRemove = prevPosition;
            adapter.notifyItemChanged(prevPosition);
            adapter.notifyItemChanged(manualTimestampPositionToAdd);
        }
    }

    private class BottomMarginAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        private View view;

        public BottomMarginAnimationListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            float margin = (Float) animator.getAnimatedValue();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = (int) margin;
            view.requestLayout();
        }
    }
}
