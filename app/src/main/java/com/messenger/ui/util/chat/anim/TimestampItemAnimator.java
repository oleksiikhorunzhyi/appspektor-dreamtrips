package com.messenger.ui.util.chat.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messenger.ui.adapter.holder.chat.MessageViewHolder;
import com.messenger.ui.adapter.inflater.chat.ChatTimestampInflater;
import com.messenger.ui.anim.SimpleAnimatorListener;

import java.util.HashMap;

public class TimestampItemAnimator extends SimpleItemAnimator {

   private ChatTimestampInflater chatTimestampInflater;

   private HashMap<RecyclerView.ViewHolder, ValueAnimator> pendingAnimations = new HashMap<>();
   private HashMap<RecyclerView.ViewHolder, ValueAnimator> runningAnimations = new HashMap<>();

   public TimestampItemAnimator(ChatTimestampInflater chatTimestampInflater) {
      this.chatTimestampInflater = chatTimestampInflater;
   }

   @Override
   public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
      return true;
   }

   @Override
   public boolean animateRemove(RecyclerView.ViewHolder holder) {
      return false;
   }

   @Override
   public boolean animateAdd(RecyclerView.ViewHolder holder) {
      return false;
   }

   @Override
   public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
      return false;
   }

   @Override
   public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {

      TimestampAnimationType type = chatTimestampInflater.popPendingAnimation(newHolder.getAdapterPosition());
      if (type == null) {
         return false;
      }

      MessageViewHolder holder = (MessageViewHolder) newHolder;
      pendingAnimations.put(holder, getAnimator(type, holder));

      return true;
   }

   private ValueAnimator getAnimator(TimestampAnimationType type, MessageViewHolder holder) {
      switch (type) {
         case SLIDE_DOWN:
            return getSlideDownAnimator(holder);
         case SLIDE_UP:
            return getSlideUpAnimator(holder);
         default:
            throw new IllegalArgumentException("Unknown type");
      }
   }

   private ValueAnimator getSlideUpAnimator(MessageViewHolder holder) {
      TextView dateTextView = holder.dateTextView;
      if (dateTextView.getMeasuredHeight() == 0) {
         dateTextView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      }

      ValueAnimator animator = ValueAnimator.ofFloat(-dateTextView.getMeasuredHeight(), 0);
      animator.addUpdateListener(new BottomMarginAnimationListener(dateTextView));
      animator.addListener(new SimpleAnimatorListener() {
         @Override
         public void onAnimationStart(Animator animator) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) dateTextView.getLayoutParams();
            params.bottomMargin = -dateTextView.getMeasuredHeight();
            dateTextView.setVisibility(View.VISIBLE);
         }

         @Override
         public void onAnimationEnd(Animator animator) {
            dispatchChangeFinished(holder, false);
         }
      });
      return animator;
   }

   private ValueAnimator getSlideDownAnimator(MessageViewHolder holder) {
      View dateTextView = holder.dateTextView;
      ValueAnimator animator = ValueAnimator.ofFloat(0, -dateTextView.getMeasuredHeight());
      animator.addUpdateListener(new BottomMarginAnimationListener(dateTextView));
      animator.addListener(new SimpleAnimatorListener() {
         @Override
         public void onAnimationStart(Animator animator) {
            dateTextView.setVisibility(View.VISIBLE);
         }

         @Override
         public void onAnimationEnd(Animator animator) {
            dateTextView.setVisibility(View.GONE);
            dispatchChangeFinished(holder, false);
         }
      });
      return animator;
   }

   @Override
   public void runPendingAnimations() {
      for (RecyclerView.ViewHolder viewHolder : pendingAnimations.keySet()) {
         ValueAnimator valueAnimator = pendingAnimations.get(viewHolder);
         valueAnimator.start();
         runningAnimations.put(viewHolder, valueAnimator);
      }
      pendingAnimations.clear();
   }

   @Override
   public void endAnimation(RecyclerView.ViewHolder item) {
      ValueAnimator animator = runningAnimations.remove(item);
      if (animator != null) animator.cancel();
   }

   @Override
   public void endAnimations() {
      for (ValueAnimator animator : runningAnimations.values()) {
         animator.cancel();
      }
      runningAnimations.clear();
   }

   @Override
   public boolean isRunning() {
      for (ValueAnimator animator : runningAnimations.values()) {
         if (animator.isRunning()) return true;
      }
      return false;
   }

   protected static class BottomMarginAnimationListener implements ValueAnimator.AnimatorUpdateListener {

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
